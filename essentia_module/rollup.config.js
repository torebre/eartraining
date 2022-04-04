import {nodeResolve} from "@rollup/plugin-node-resolve"
import rollupTypescript from "@rollup/plugin-typescript"

const output = {
    dir: "dist",
    sourcemap: true,
}

const plugins = [
    nodeResolve({preferBuiltins: false, browser: true}),
    rollupTypescript(),
]

export default [
    {
        input: "src/essentiaModule.ts",
        output: {
            ...output,
            format: "iife",
        },
        plugins,
    },
]
