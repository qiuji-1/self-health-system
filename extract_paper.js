const fs = require('fs');
const path = require('path');

// Copy docx to zip (docx is just a zip with different extension)
const docxPath = path.join(__dirname, 'docs', '毕业论文改-2.docx');
const zipPath = path.join(__dirname, 'temp_paper.zip');
const outputPath = path.join(__dirname, 'temp_paper_content.txt');

fs.copyFileSync(docxPath, zipPath);
console.log('Copied docx to zip');

const { execSync } = require('child_process');
try {
    // Extract using the copied zip file
    const extractPath = path.join(__dirname, 'temp_paper_extract');
    execSync(`powershell -Command "Expand-Archive -Path '${zipPath}' -DestinationPath '${extractPath}' -Force"`, { encoding: 'utf8' });
    console.log('Extracted with PowerShell');
    
    const xmlDoc = fs.readFileSync(path.join(extractPath, 'word', 'document.xml'), 'utf8');
    
    // Extract text from <w:t> tags
    const regex = /<w:t[^>]*>([^<]+)<\/w:t>/g;
    let match;
    const texts = [];
    while ((match = regex.exec(xmlDoc)) !== null) {
        texts.push(match[1]);
    }
    
    fs.writeFileSync(outputPath, texts.join('\n'));
    console.log('Extracted', texts.length, 'text segments to temp_paper_content.txt');
} catch (e) {
    console.error('Error:', e.message);
}
