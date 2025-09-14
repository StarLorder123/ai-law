export const toolBox: { [key: string]: any } = {};

export const toolsArray: any[] = [];

export function addTool2Box(key: string, tool: any) {
    toolBox[key] = tool;
    toolsArray.push(tool);
}
