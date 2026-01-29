import os
import re

JSP_DIR = r"d:\IDEA\IntelliJ IDEA 2024.1\workplaces\work\src\main\webapp\WEB-INF\jsp"
OUTPUT_DIR = r"d:\IDEA\IntelliJ IDEA 2024.1\workplaces\work\work121\src\components"

# Already converted manually
SKIP_FILES = ["index.jsp"]

def to_camel_case(snake_str):
    components = snake_str.split('_')
    return ''.join(x.title() for x in components)

def parse_jsp(file_path):
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()

    # Extract Title
    title_match = re.search(r'<title>(.*?)</title>', content)
    title = title_match.group(1) if title_match else "Report"

    # Extract Style
    style_match = re.search(r'<style>(.*?)</style>', content, re.DOTALL)
    style_content = style_match.group(1) if style_match else ""
    
    # Clean up style (remove body width if needed, but keeping it is safer for layout)
    # Remove @media print .no-print display:none duplication if handled in template
    
    # Extract Body Content (excluding scripts)
    body_match = re.search(r'<body>(.*?)</body>', content, re.DOTALL)
    if not body_match:
        return None
    body_content = body_match.group(1)
    
    # Remove <script> tags from body
    body_content = re.sub(r'<script.*?>.*?</script>', '', body_content, flags=re.DOTALL)
    
    # Extract Action URLs
    gen_action = ""
    prev_action = ""
    gen_match = re.search(r'action\s*=\s*[\'"](.*?)generate[\'"]', content)
    if gen_match: gen_action = gen_match.group(1) + "generate"
    prev_match = re.search(r'action\s*=\s*[\'"](.*?)preview[\'"]', content)
    if prev_match: prev_action = prev_match.group(1) + "preview"

    # Process Form
    form_data = {}
    
    # Helper to replace inputs
    def replace_input(match):
        full_tag = match.group(0)
        attrs = match.group(1)
        
        # If v-model already exists, skip
        if "v-model" in attrs:
            return full_tag
        
        name_match = re.search(r'name=["\'](.*?)["\']', attrs)
        if not name_match: return full_tag
        
        name = name_match.group(1)
        
        # Check type
        type_match = re.search(r'type=["\'](.*?)["\']', attrs)
        input_type = type_match.group(1) if type_match else "text"
        
        # Remove type from attrs to avoid duplication
        attrs = re.sub(r'type=["\'].*?["\']', '', attrs)

        if input_type == "text":
            form_data[name] = ""
            return f'<input type="text" v-model="formData.{name}" {attrs}>'
        elif input_type == "checkbox":
            if name not in form_data: form_data[name] = []
            return f'<input type="checkbox" v-model="formData.{name}" {attrs}>'
        elif input_type == "radio":
            if name not in form_data: form_data[name] = ""
            return f'<input type="radio" v-model="formData.{name}" {attrs}>'
        
        return full_tag

    def replace_textarea(match):
        attrs = match.group(1)
        content = match.group(2)
        name_match = re.search(r'name=["\'](.*?)["\']', attrs)
        if not name_match: return f'<textarea {attrs}>{content}</textarea>'
        
        name = name_match.group(1)
        form_data[name] = content.strip()
        return f'<textarea v-model="formData.{name}" {attrs}></textarea>'

    # Process Loop
    # Pattern: <% for(int i=0; i<20; i++) { %> ... <% } %>
    # We will replace this with <template v-for="(n, i) in 20" :key="i"> ... </template>
    # And replace name="..._<%=i%>" with :name="'..._' + i" and v-model="formData['..._' + i]"
    
    def process_loops(text):
        # Updated pattern to match INNERMOST loops first
        # We look for a loop start, followed by content that does NOT contain another loop start, followed by loop end
        # Using (?:(?!<%[\s]*for).)*? to match content without nested loops
        
        loop_pattern = r'<%[\s]*for\s*\(\s*int\s*([a-zA-Z0-9]+)\s*=\s*(\d+)\s*;\s*\1\s*(<=|<)\s*(\d+)\s*;\s*\1\+\+\s*\)\s*\{\s*%>( (?: (?!<%[\s]*for). )*? )<%[\s]*\}\s*%>'
        
        # We need to compile with DOTALL and VERBOSE (for whitespace in regex)
        # But re.sub doesn't easily support repeated application until no match.
        # We'll use a while loop.
        
        regex = re.compile(loop_pattern, re.DOTALL | re.VERBOSE)
        
        def loop_replacer_vue(match):
            var_name = match.group(1) # e.g. 'i'
            start_val = int(match.group(2)) # e.g. 0 or 1
            operator = match.group(3) # < or <=
            end_val = int(match.group(4)) # e.g. 20 or 25
            inner_html = match.group(5)
            
            count = 0
            if operator == "<":
                count = end_val - start_val
            elif operator == "<=":
                count = end_val - start_val + 1
                
            idx_var = f"{var_name}_idx"
            
            # Replacement function for inner expressions
            def replace_expr(m):
                val_expr = idx_var if start_val == 0 else f"({idx_var} + {start_val})"
                return f':name="\'{m.group(1)}_\' + {val_expr}" v-model="formData[\'{m.group(1)}_\' + {val_expr}]"'

            inner_html = re.sub(
                r'name=["\'](.*?)_<%=\s*' + var_name + r'\s*%>["\']',
                replace_expr,
                inner_html
            )
             
            # Replace any standalone <%=i%>
            val_expr_display = idx_var if start_val == 0 else f"({idx_var} + {start_val})"
            inner_html = re.sub(r'<%=\s*' + var_name + r'\s*%>', f'{{{{ {val_expr_display} }}}}', inner_html)
            
            return f'<template v-for="(n, {idx_var}) in {count}" :key="{idx_var}">{inner_html}</template>'

        # Keep replacing until no more matches found (handling nesting from inside out)
        current_text = text
        while True:
            new_text = regex.sub(loop_replacer_vue, current_text)
            if new_text == current_text:
                break
            current_text = new_text
            
        return current_text

    # 1. Process Loops First
    processed_body = process_loops(body_content)
    
    # 2. Process Inputs/Textareas (non-dynamic ones)
    processed_body = re.sub(r'<input(.*?)>', replace_input, processed_body)
    processed_body = re.sub(r'<textarea(.*?)>(.*?)</textarea>', replace_textarea, processed_body, flags=re.DOTALL)
    
    # 3. Clean up other JSP scriptlets
    processed_body = re.sub(r'<%--.*?--%>', '', processed_body, flags=re.DOTALL)
    
    # 4. Handle id="pdfForm" to ref="pdfForm"
    processed_body = processed_body.replace('id="pdfForm"', 'id="pdfForm" ref="pdfForm"')
    processed_body = processed_body.replace('id="entrustmentForm"', 'id="entrustmentForm" ref="pdfForm"')
    
    # 5. Replace onclick
    processed_body = processed_body.replace('onclick="window.print()"', '@click="printDocument"')
    processed_body = processed_body.replace('onclick="generatePdf()"', '@click="generatePdf"')
    processed_body = processed_body.replace('onclick="previewPdf()"', '@click="previewPdf"')
    
    # 6. Fix "No Print" link
    processed_body = processed_body.replace('<a href="/"', '<a href="/"') # Keep as is, or use router-link if needed. User said "work121" which is Vue, maybe router-link? For now keep <a> to root.

    return {
        "title": title,
        "style": style_content,
        "template": processed_body,
        "formData": form_data,
        "actions": {"generate": gen_action, "preview": prev_action}
    }

def generate_vue_file(component_name, data):
    # Detect dynamic fields from loops to initialize them
    # Simple heuristic: scan template for formData['...']
    dynamic_fields_init = ""
    
    # Find all v-model="formData['...']"
    # Match: formData['prefix_' + var] OR formData['prefix_' + (var + offset)]
    # We want to capture 'prefix_' and 'var'
    
    # Simple regex for var (allowing _, and maybe expressions starting with ( )
    # This is getting complex to parse from string.
    # Let's try to match the two common patterns we generated:
    # 1. formData['prefix_' + var]
    # 2. formData['prefix_' + (var + offset)]
    
    dynamic_matches = []
    
    # Pattern 1: Simple var
    p1 = re.findall(r"formData\['(.*?)' \+ ([a-zA-Z0-9_]+)\]", data['template'])
    dynamic_matches.extend(p1)
    
    # Pattern 2: Expression (var + offset)
    # We want to extract 'var' from '(var + ...)'
    p2_raw = re.findall(r"formData\['(.*?)' \+ \(([a-zA-Z0-9_]+) \+ \d+\)\]", data['template'])
    dynamic_matches.extend(p2_raw)
    
    # Group by variable (loop index)
    loops = {}
    for prefix, var in dynamic_matches:
        if var not in loops: loops[var] = set()
        loops[var].add(prefix)
        
    # Construct init logic
    # We need to guess the loop count. 
    # For now, I'll hardcode a check or parse it again. 
    # Actually, simpler approach: use Proxy or just initialize what we found in static analysis of loops?
    # Parsing the loop count again from the template is hard because it's transformed.
    # Let's use a simpler approach: Initialize dynamic fields in onMounted based on a generic loop if possible, 
    # OR just don't initialize them (Vue 3 reactive proxies might handle setting new props, but v-model on undefined might warn).
    # Better: Analyze the original JSP loop counts again or pass them from parse_jsp.
    
    # Let's improve parse_jsp to return loop info.
    # For now, I will just generate a generic init loop for "i" up to 20 or 100 if "i" is detected.
    
    init_script = ""
    if loops:
        for var, prefixes in loops.items():
            # Heuristic: if var is 'i', assume 20 or 8 based on common files. 
            # This is risky. Let's just create an empty loop structure for the user to verify/fix.
            init_script += f"\n  // Initialize dynamic fields for loop variable '{var}'\n  // Please verify the loop count match the template\n  for (let {var} = 0; {var} < 50; {var}++) {{\n"
            for p in prefixes:
                # p usually includes the underscore if it was captured from 'prefix_' + var
                # But let's be safe
                if p.endswith('_'):
                    init_script += f"    formData['{p}' + {var}] = ''\n"
                else:
                    init_script += f"    formData['{p}_' + {var}] = ''\n"
            init_script += "  }\n"

    form_data_str = "{\n"
    for k, v in data['formData'].items():
        if isinstance(v, list):
            form_data_str += f"  {k}: [],\n"
        else:
            safe_v = v.replace('\n', '\\n').replace("'", "\\'")
            form_data_str += f"  {k}: '{safe_v}',\n"
    form_data_str += "}"

    # Process styles
    style_content = data['style']
    container_class = f".{component_name[0].lower() + component_name[1:]}-container"
    
    # Replace 'body' selector with container class
    # But be careful with '@media print { body ... }'
    # And 'tbody', 'tbody tr' etc.
    
    # Simple replacement of 'body ' or 'body{' might be risky if 'tbody' exists.
    # Regex replace 'body' that is not preceded by 't' (like tbody)
    # Pattern: (?<!t)body
    
    style_content = re.sub(r'(?<!t)body', container_class, style_content)
    
    vue_content = f"""<template>
  <div class="{component_name[0].lower() + component_name[1:]}-container">
{data['template']}
  </div>
</template>

<script setup>
import {{ reactive, ref, onMounted }} from 'vue'

const pdfForm = ref(null)

const formData = reactive({form_data_str})

onMounted(() => {{
{init_script}
}})

const printDocument = () => {{
  window.print()
}}

const generatePdf = () => {{
  if (pdfForm.value) {{
    pdfForm.value.action = '{data['actions']['generate']}'
    pdfForm.value.target = '_blank'
    pdfForm.value.submit()
  }}
}}

const previewPdf = () => {{
  if (pdfForm.value) {{
    pdfForm.value.action = '{data['actions']['preview']}'
    pdfForm.value.target = '_blank'
    pdfForm.value.submit()
  }}
}}
</script>

<style scoped>
{style_content}
</style>
"""
    return vue_content
    return vue_content

def main():
    if not os.path.exists(OUTPUT_DIR):
        os.makedirs(OUTPUT_DIR)

    for filename in os.listdir(JSP_DIR):
        if filename in SKIP_FILES or not filename.endswith(".jsp"):
            continue
            
        print(f"Processing {filename}...")
        jsp_path = os.path.join(JSP_DIR, filename)
        
        parsed_data = parse_jsp(jsp_path)
        if not parsed_data:
            print(f"Skipping {filename} (no body found)")
            continue
            
        component_name = to_camel_case(filename.replace(".jsp", ""))
        vue_file_path = os.path.join(OUTPUT_DIR, f"{component_name}.vue")
        
        vue_content = generate_vue_file(component_name, parsed_data)
        
        with open(vue_file_path, 'w', encoding='utf-8') as f:
            f.write(vue_content)
        print(f"Generated {vue_file_path}")

if __name__ == "__main__":
    main()
