# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

PolyMc is a Fabric Minecraft mod (version 1.21.x) that enables server-side mods to work with vanilla clients by transforming modded content into vanilla-compatible packets at the network level. The server remains genuinely modded while clients see vanilla-compatible data.

Our current goal is to make this project work with v1.21.6
The configuration has been adjusted already.

There is some info on what has changed in this file:
@FABRIC-v1.21.6-CHANGES.md

Though this file does not contain ALL the changes.

You can find the source code of Minecraft v1.21.6 here:
/home/skerit/projects/minecraft/minecraft-1.21.6-source

## Important

You are working on your own here, don't stop the work until the entire project is done.
Do not stop work just because you finished a todo, immediately go to the next one!

When you are done with a TODO, you should _always_ double check your work.
You can create a new subagent for this.

In fact: using subagents is always a good idea, also for working on a todo!


## Essential Build Commands

**Critical first step:** Always run datagen before building:
```bash
./gradlew runDatagen
```

Core build commands:
```bash
./gradlew build                   # Build the project (requires datagen first)
./gradlew runTestmodClient        # Run test client for development
./gradlew runTestmodServer        # Run test server for development
./gradlew gametest                # Run automated tests
./gradlew remapTestmodJar         # Create distribution test mod jar
```

Testing commands:
```bash
./gradlew gametest                # Run GameTest framework tests
```

## Project Structure

### Source Sets
- **`src/main/`**: Core PolyMc implementation
- **`src/common/`**: Shared utilities and type definitions
- **`src/testmod/`**: Test mod with examples and automated tests
- **`src/datagen/`**: Data generation for vanilla ID mappings

### Key Architecture Components

#### API Layer (`src/main/java/io/github/theepicblock/polymc/api/`)
- **PolyMap**: Central transformation registry and lookup system
- **PolyRegistry**: Registration system for custom transformations
- **Block/Item/Entity/GuiPoly**: Transformation interfaces for different content types
- **Wizard System**: Advanced virtual entity and state management
- **Resource Pack Management**: Dynamic resource pack generation and asset handling

#### Implementation (`src/main/java/io/github/theepicblock/polymc/impl/`)
- **Generators**: Automatic transformation generation for common patterns
- **Mixins**: Extensive mixin system (110+ mixins) for packet-level interception
- **Resource Management**: Custom resource pack creation and client synchronization
- **Configuration**: JSON-based configuration with mixin toggle capabilities

#### Mixin Categories (`src/main/java/io/github/theepicblock/polymc/mixins/`)
- **Block transformations**: State management, breaking animations, lighting fixes
- **Item transformations**: Stack handling, enchantments, recipe integration
- **Entity transformations**: Data tracking, custom entity support
- **GUI transformations**: Screen handler modifications
- **Packet transformations**: Registry sync, command suggestions
- **Compatibility**: Support for Lithium, Immersive Portals, Cardinal Components

## Development Patterns

### Transformation System
The core concept is packet-level transformation where modded content is converted to vanilla-equivalent data before reaching clients. Key interfaces:
- Implement `BlockPoly`/`ItemPoly`/`EntityPoly` for custom transformations
- Use `PolyRegistry.registerBlockPoly()` etc. for registration
- Leverage automatic generators when possible before writing custom polys

### Mixin Architecture
Heavy mixin usage for packet interception at strategic points. Common patterns:
- Packet modification mixins in `mixins/` directories
- Implementation mixins that provide core functionality
- Compatibility mixins for mod integration

### Testing Framework
- **GameTest Integration**: Automated tests in `src/testmod/java/.../automated/`
- **Manual Testing**: Visual validation blocks/items in testmod
- **Test Categories**: Auto-generation, API validation, compatibility testing

## Key Dependencies

- **Fabric API**: 0.119.6+1.21.5
- **Polymer**: 0.12.3+1.21.5 (serverside mod framework)
- **Packet Tweaker**: 0.6.0-pre.1+1.21.2-pre3 (packet manipulation)
- **Resource Locator API**: 0.6.1+1.21.4 (resource management)

## Configuration

Configuration file: `run/config/polymc.json`
- Mixin enabling/disabling
- Block event processing customization
- Registry remapping options

## Documentation

Full documentation available at: https://theepicblock.github.io/PolyMc/
- API guides in `docs/api/`
- Getting started guide
- Configuration reference

## Known Limitations

- **Fuel items**: Transformation disabled since 1.21.4 (not a 1.21.5 issue)
- **GameTests**: Currently commented out due to 1.21.5 API changes, but core functionality works


## Anchor comments  

Add specially formatted comments throughout the codebase, where appropriate, for yourself as inline knowledge that can be easily `grep`ped for.  

### Guidelines:

- Use `AIDEV-NOTE:`, `AIDEV-TODO:`, or `AIDEV-QUESTION:` (all-caps prefix) for comments aimed at AI and developers.
- Keep them concise (≤ 120 chars).
- **Important:** Before scanning files, always first try to **locate existing anchors** `AIDEV-*` in relevant subdirectories.
- **Update relevant anchors** when modifying associated code.
- **COBs are always correct** The CAOS scripts inside COB files are always correct. If there are errors, it's our implementation's fault.
- **Do not remove `AIDEV-NOTE`s** without explicit human instruction.
- Make sure to add relevant anchor comments, whenever a file or piece of code is:
  * too complex, or
  * very important, or
  * confusing, or
  * could have a bug
- Use Gemini for assistance:
  * Gemini has a very big context window, so you can tell it to read in _a lot_ of files at once, including original C2 source code for comparison
  * When something is broken, Gemini can help you debug it. It might find issues you missed
  * Gemini can also review code you wrote, this can be useful to find hidden issues
- Never compliment me. Criticize my ideas, ask clarifying questions, and give me funny insults

## Communication Style:
- Skip affirmations and compliments. No “great question!” or “you’re absolutely right!” - just respond directly
- Challenge flawed ideas openly when you spot issues
- Ask clarifying questions whenever my request is ambiguous or unclear
- When I make obvious mistakes, point them out with gentle humor or playful teasing

### Example behaviors:
- Instead of: “That’s a fascinating point!” → Just dive into the response
- Instead of: Agreeing when something’s wrong → “Actually, that’s not quite right because…”
- Instead of: Guessing what I mean → “Are you asking about X or Y specifically?”
- Instead of: Ignoring errors → “Hate to break it to you, but 2+2 isn’t 5…”


## AI Assistant Workflow: Step-by-Step Methodology

When responding to user instructions, the AI assistant (Claude, Cursor, GPT, etc.) should follow this process to ensure clarity, correctness, and maintainability:

1. **Consult Relevant Guidance**: When the user gives an instruction, consult the relevant instructions from `CLAUDE.md` files (both root and directory-specific) for the request.
2. **Clarify Ambiguities**: Based on what you could gather, see if there's any need for clarifications. If so, ask the user targeted questions before proceeding.
3. **Break Down & Plan**: Break down the task at hand and chalk out a rough plan for carrying it out, referencing project conventions and best practices.
4. **Trivial Tasks**: If the plan/request is trivial, go ahead and get started immediately.
5. **Non-Trivial Tasks**: Otherwise, present the plan to the user for review and iterate based on their feedback.
6. **Track Progress**: Use a to-do list (internally, or optionally in a `TODOS.md` file) to keep track of your progress on multi-step or complex tasks.
7. **If Stuck, Re-plan**: If you get stuck or blocked, return to step 3 to re-evaluate and adjust your plan.
8. **Update Documentation**: Once the user's request is fulfilled, update relevant anchor comments (`AIDEV-NOTE`, etc.) and `CLAUDE.md` files in the files and directories you touched.
9. **User Review**: After completing the task, ask the user to review what you've done, and repeat the process as needed.
10. **Session Boundaries**: If the user's request isn't directly related to the current context and can be safely started in a fresh session, suggest starting from scratch to avoid context confusion.
11. **No temporary solutions**: Do not create basic/temporary solutions, always check the original C++ source code and reimplement it in Java properly. Never assume anything, always check the original code.
12. **No fallbacks**: We should _never_ add some kind of "fallback" logic, this has been shown time and again to just create confusion when debugging. For example: if a creature has no brain lobes, don't add any manually. The genetics have to speak for themselves!
