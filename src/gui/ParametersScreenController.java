package gui;

import de.lessvoid.nifty.NiftyEventSubscriber;
import de.lessvoid.nifty.controls.ButtonClickedEvent;
import de.lessvoid.nifty.controls.ScrollbarChangedEvent;
import de.lessvoid.nifty.controls.TextFieldChangedEvent;
import de.lessvoid.nifty.screen.DefaultScreenController;

public class ParametersScreenController extends DefaultScreenController{

    private float mAirDensity;
    private float mGravity;
    private boolean firstCallGravity = true;
    private boolean firstCallDensity= true;


    @NiftyEventSubscriber(id="gravity_scroll")
    public void onScrollBarChanged1(final String id, final ScrollbarChangedEvent event) {
        mGravity = event.getValue();
        firstCallGravity = false;
    }
    @NiftyEventSubscriber(id="air_density_scroll")
    public void onScrollBarChanged2(final String id, final ScrollbarChangedEvent event) {
        mAirDensity = event.getValue();
        firstCallDensity= false;

    }
    @NiftyEventSubscriber(id="vsb3")
    public void onScrollBarChanged3(final String id, final ScrollbarChangedEvent event) {

    }
    @NiftyEventSubscriber(id="vsb4")
    public void onScrollBarChanged4(final String id, final ScrollbarChangedEvent event) {

    }

    public float getGravity() {
        if (firstCallGravity) {
            return (float) -3.174;
        }
        return - this.mGravity;
    }

    public float getAirDensity() {
        if (firstCallDensity) {
            return (float) 0.0023769f;
        }
        return this.mAirDensity;
    }
}
