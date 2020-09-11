package gui;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.nulldevice.NullSoundDevice;
import de.lessvoid.nifty.render.batch.BatchRenderDevice;
import de.lessvoid.nifty.renderer.lwjgl.input.LwjglInputSystem;
import de.lessvoid.nifty.renderer.lwjgl.render.LwjglBatchRenderBackendCoreProfileFactory;
import de.lessvoid.nifty.spi.time.impl.AccurateTimeProvider;
import org.lwjgl.opengl.Display;

public class GuiRenderer {

    private Nifty nifty = null;
    private LwjglInputSystem inputSystem = null;

    public Nifty initNifty() {

        try {
            inputSystem = initInput();
            nifty = initNifty(inputSystem);
        } catch (Exception e) {
            e.printStackTrace();
        }
        nifty.fromXml("res/main.xml", "start");
        nifty.gotoScreen("start");

        return nifty;
    }
    private static LwjglInputSystem initInput() throws Exception {
        LwjglInputSystem inputSystem = new LwjglInputSystem();
        inputSystem.startup();
        return inputSystem;
    }

    private static Nifty initNifty(final LwjglInputSystem inputSystem) throws Exception {
        return new Nifty(
                new BatchRenderDevice(LwjglBatchRenderBackendCoreProfileFactory.create()),
                new NullSoundDevice(),
                inputSystem,
                new AccurateTimeProvider());
    }

    public void shutDown() {
        inputSystem.shutdown();
        Display.destroy();
        System.exit(0);
    }

}
