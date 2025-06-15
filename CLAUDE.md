# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

PolyMc is a Fabric Minecraft mod (version 1.21.5) that enables server-side mods to work with vanilla clients by transforming modded content into vanilla-compatible packets at the network level. The server remains genuinely modded while clients see vanilla-compatible data.

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