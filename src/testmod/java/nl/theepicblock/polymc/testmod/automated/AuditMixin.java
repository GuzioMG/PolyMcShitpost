package nl.theepicblock.polymc.testmod.automated;

import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.test.TestContext;
import org.spongepowered.asm.mixin.MixinEnvironment;

public class AuditMixin  {
    @GameTest()
    public void testMixin(TestContext ctx) {
        MixinEnvironment.getCurrentEnvironment().audit();
        ctx.complete();
    }
}
