# II Technology

II Technology is a Minecraft 1.21.1 / NeoForge addon for Applied Energistics 2. It adds the 2i
material chain, optional Mekanism silica chemistry, a self-contained infinite storage cell, and a
configurable 2i crafting CPU with an automatically sliced storage pool.

## Requirements

- Minecraft 1.21.1
- NeoForge
- Applied Energistics 2 19.2.17 or newer

Optional integrations:

- Mekanism: silica chemical and processing recipes.
- Data_Energistics: its explosives can drop 2i when they destroy sand.
- AE2 Lightning Tech: 2i materials can carry or summon lightning.

## Features

### 2i material chain

The mod adds four fire-resistant materials: `2i`, `2i (2)`, `2i (3)`, and `2i (4)`.

With Mekanism installed, the chain is:

- `sand -> silica` in an Oxidation Chamber.
- `2i + silica -> 2i (2)` in a Metallurgic Infuser.
- `2i (2) + milk + silica -> 2i (3)` in a Chemical Reaction Chamber.
- `2i (3) -> 2i (4)` via Mekanism Sun assembly, with a Mekanism anti-proton
  nucleosynthesizing fallback when Mekanism Sun is not installed.

Without Mekanism, `2i`, `2i (2)`, and `2i (3)` can be smelted into glass in a vanilla furnace.

### Infinite ii Cell

The infinite ii cell is an AE2 storage cell that provides infinite extraction for:

- `2i`
- `minecraft:sand`
- all 16 vanilla concrete powders

Marked items never deplete on extraction and are voided on insertion. The cell reports `∞` in
AE2 terminals and has no idle power draw.

### 2i Crafting CPU

The 2i CPU is a single-block AE2 crafting CPU.

- Shift-right-click to edit its storage capacity in bytes, from 1 to `Long.MAX_VALUE`.
- Parallel is fixed at 1.
- Crafting jobs automatically reserve the bytes they need from the storage pool, and return
  them when the job finishes.
- The block connects to AE2 networks on all six faces.

### Recipes and drops

- `2i (4)` can be combined with sand and an AE2 crafting unit to craft the 2i CPU.
- Vanilla TNT, AE2 Tiny TNT, and Data_Energistics explosives can leave a 2i behind when they
  destroy plain sand.
- A hidden advancement is granted when a 2i drop occurs.

## Building

The project uses the NeoForge ModDevGradle plugin and Java 21.

```powershell
gradle.bat build
```

The built jar is written to `build/libs/IITechnology-<version>.jar`.

`libs/` contains compile-only local dependencies used for offline source imports. They are not
packaged into the mod jar.

## License

MIT. See [LICENSE](LICENSE).
