import fs from "node:fs/promises";
import { FileBlob, SpreadsheetFile } from "@oai/artifact-tool";

const input = process.argv[2];
const output = process.argv[3];

if (!input || !output) {
  throw new Error("Usage: node render-xlsx.mjs input.xlsx output.png");
}

const blob = await FileBlob.load(input);
const workbook = await SpreadsheetFile.importXlsx(blob);

const overview = await workbook.inspect({
  kind: "workbook,sheet,table",
  maxChars: 4000,
  tableMaxRows: 8,
  tableMaxCols: 8,
});

const region = await workbook.inspect({
  kind: "region",
  sheetId: "Cotizacion",
  range: "A1:G20",
  maxChars: 5000,
});

const errors = await workbook.inspect({
  kind: "match",
  searchTerm: "#REF!|#DIV/0!|#VALUE!|#NAME\\?|#N/A",
  options: { useRegex: true, maxResults: 100 },
  summary: "formula error scan",
});

const preview = await workbook.render({
  sheetName: "Cotizacion",
  range: "A1:G20",
  scale: 2,
  format: "png",
});

await fs.writeFile(output, new Uint8Array(await preview.arrayBuffer()));

console.log(JSON.stringify({
  overview: overview.ndjson,
  region: region.ndjson,
  errors: errors.ndjson,
  output,
}, null, 2));
