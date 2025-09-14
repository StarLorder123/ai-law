import { tool } from "@langchain/core/dist/tools";
import { z } from "zod";

export const planTool = tool(
    async ({ plan, content, steps }) => {
        console.log(`plan:${plan},content:${content},steps:${JSON.stringify(steps)}`);
    },
    {
        name: "plan",
        description: "Plan What User Need",
        schema: z.object(
            {
                plan: z.string().describe("The name of plan"),
                content: z.string().describe("The content of plan"),
                steps: z.array(
                    z.string().describe("Each step of plan")
                ).describe("The steps of plan")
            }
        ),
    }
)