package gui;

import de.lessvoid.nifty.NiftyEventSubscriber;
import de.lessvoid.nifty.controls.ButtonClickedEvent;
import de.lessvoid.nifty.controls.ScrollbarChangedEvent;
import de.lessvoid.nifty.screen.DefaultScreenController;

public class ParametersScreenController extends DefaultScreenController{
    // gravity and other values will be set here
    // float gravity = event.getValue(); and so on


    @NiftyEventSubscriber(id="vsb1")
    public void onScrollBarChanged1(final String id, final ScrollbarChangedEvent event) {

    }
    @NiftyEventSubscriber(id="vsb2")
    public void onScrollBarChanged2(final String id, final ScrollbarChangedEvent event) {

    }
    @NiftyEventSubscriber(id="vsb3")
    public void onScrollBarChanged3(final String id, final ScrollbarChangedEvent event) {

    }
    @NiftyEventSubscriber(id="vsb4")
    public void onScrollBarChanged4(final String id, final ScrollbarChangedEvent event) {

    }

}
