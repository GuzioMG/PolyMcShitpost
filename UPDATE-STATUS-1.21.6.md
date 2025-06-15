# PolyMc 1.21.6 Update Status

## Current Status

The project has been partially updated to work with Minecraft 1.21.6-rc1, but there are still some issues that need to be resolved.

### Completed Work

1. **Configuration Updates**
   - Updated `gradle.properties` with Minecraft 1.21.6-rc1 versions
   - Updated Polymer dependency to 0.13.0-pre.1+1.21.6-pre2 (fixes DimensionType constructor issue)
   - Updated `fabric.mod.json` with correct Minecraft version

2. **Fixed Mixins**
   - FallingBlockEntityMixin: Updated to use simpler method descriptors
   - Network handler mixins: Updated all packet send injections to use `ChannelFutureListener`

3. **PacketCodecs Mixins** (Partially Fixed)
   - Updated to use bridge method signatures `encode(Object, Object)` to handle type erasure
   - The mixins compile without warnings but still fail at runtime
   - Currently disabled in `polymc.mixins.json`

### Known Issues

1. **PacketCodecs Mixins Runtime Failure**
   - The anonymous class numbers ($31, $32, $33) may be incorrect for 1.21.6
   - Requires bytecode analysis to determine correct targets
   - Alternative approach: Target the factory methods instead of anonymous classes

2. **Server Startup Hang**
   - Server generates polymap successfully but then hangs
   - May be related to missing dependencies or compatibility issues

3. **Build Warnings**
   - Several mixins show "Unable to locate method mapping" warnings
   - RemoveTickerOnUnloadMixin is auto-disabled
   - Some codec-related mixins have mapping issues

### Dependencies Status

- **Fabric API**: ✅ Updated to 0.127.0+1.21.6
- **Polymer**: ✅ Updated to 0.13.0-pre.1+1.21.6-pre2
- **Packet Tweaker**: ⚠️ Still on 0.6.0-pre.1+1.21.2-pre3 (may need update)
- **Resource Locator API**: ⚠️ Still on 0.6.1+1.21.4 (may need update)
- **Other dependencies**: Need to check for 1.21.6 compatibility

### Next Steps

1. **Fix PacketCodecs Mixins**
   - Use bytecode analysis to find correct anonymous class numbers
   - Consider alternative mixin strategies (target factory methods)
   - Test with runtime verification

2. **Investigate Server Hang**
   - Check logs for specific errors
   - Test with minimal configuration
   - Verify all dependencies are compatible

3. **Update Remaining Dependencies**
   - Check for newer versions of packet-tweaker
   - Update resource-locator-api if available
   - Test with updated dependencies

4. **Run Full Test Suite**
   - Execute gametest suite once server runs
   - Verify all PolyMc features work correctly
   - Test with vanilla clients

### Build Instructions

```bash
# Clean build with dependency refresh
./gradlew clean build --refresh-dependencies

# Run datagen (required before building)
./gradlew runDatagen

# Test server
./gradlew runTestmodServer

# Test client
./gradlew runTestmodClient
```

### Notes

- The project builds successfully but has runtime issues
- PacketCodecs mixins are the main blocker for full functionality
- Server startup hang needs investigation