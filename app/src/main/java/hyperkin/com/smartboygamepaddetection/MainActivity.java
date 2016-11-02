package hyperkin.com.smartboygamepaddetection;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.input.InputManager;
import android.os.Bundle;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import java.util.Vector;


public class MainActivity extends Activity implements InputManager.InputDeviceListener{
    static final private String GAME_CONTROLLER_TESTER_PKG_NAME = "com.catalyst06.gamecontrollerverifier";
    static final private String GAMEPAD_TESTER_PKG_NAME = "com.chiarly.gamepad";
    static final private String MYOLDBOY_FREE_PKG_NAME = "com.fastemulator.gbcfree";
    static final private String MYOLDBOY_PKG_NAME = "com.fastemulator.gbc";
    static final private String SMART_BOY_SERIAL_PKG_NAME = "hyperkin.smartboyserial";
    static final private int MAX_NUM_OF_CONTROLLERS = 2;
    static final private int SMARTBOY_A_BTN = 188;
    static final private int SMARTBOY_B_BTN = 189;
    static final private int SMARTBOY_START_BTN = 191;
    static final private int SMARTBOY_SELECT_BTN = 190;
    static final private int SMARTBOY_L_SHOULDER_BTN = 192;
    static final private int SMARTBOY_R_SHOULDER_BTN = 193;

    private TextView button_presses_view;
    private InputManager input_manager;
    //private InputManager.InputDeviceListener input_device_listener;
    private Vector controller_ids;
    private int previous_button_press;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button_presses_view = (TextView) findViewById(R.id.button_presses_textview);
        controller_ids = new Vector();

        input_manager = (InputManager) getSystemService(Context.INPUT_SERVICE);
        input_manager.registerInputDeviceListener(this, null);

        findControllers();

        previous_button_press = -1;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * function: deviceIsGamePad
     * This method determines if the device source provided is a gamepad.
     * @param device_source     source of the device (e.g. gamepad, joystick, keyboard, etc)
     * @return  true, if the device is a gamepad; false, otherwise
     */
    private boolean
    deviceIsGamePad(int device_source)
    {
        return ((device_source & InputDevice.SOURCE_GAMEPAD) == InputDevice.SOURCE_GAMEPAD);
    }

    /**
     * function: deviceIsJoystick
     * This method determines if the device source provided is a joystick.
     * @param device_source     source of the device (e.g. gamepad, joystick, keyboard, etc)
     * @return  true, if the device is a joystick; false, otherwise
     */
    private boolean
    deviceIsJoystick(int device_source)
    {
        return ((device_source & InputDevice.SOURCE_JOYSTICK) == InputDevice.SOURCE_JOYSTICK);
    }

    /**
     * function: findControllers
     * This method traverses through the devices connected to the host device looking
     * for gamepads or joysticks.
     * @return  true, if found at least one controller; false, otherwise
     */
    private boolean
    findControllers()
    {
        int[] device_ids = input_manager.getInputDeviceIds();
        for (int device_id : device_ids)
        {
            InputDevice device = input_manager.getInputDevice(device_id);
            int device_type_sources = device.getSources();

            if (deviceIsGamePad(device_type_sources) || deviceIsJoystick(device_type_sources)) {
                if (!controller_ids.contains(device_id)) {
                    controller_ids.add(device_id);
                    button_presses_view.append("Controller found: Device ID = " + device_id + "\n");
                }
            }
        }

        return (controller_ids.size() > 0) ? true : false;
    }

    /**
     * function: invokeMyOldBoyApp
     * This method gets call when the MyOldBoy button gets press. We create an intent to start the
     * My Old Boy app.
     */
    public void
    invokeMyOldBoyApp(View view)
    {
        PackageManager packet_manager = getPackageManager();

        // try to launch paid version of my old boy
        Intent intent_to_start_my_old_boy = packet_manager.getLaunchIntentForPackage(MYOLDBOY_PKG_NAME);
        if (intent_to_start_my_old_boy != null) {
            startActivity(intent_to_start_my_old_boy);
        }

        // try to launch free version of my old boy
        intent_to_start_my_old_boy = packet_manager.getLaunchIntentForPackage(MYOLDBOY_FREE_PKG_NAME);
        if (intent_to_start_my_old_boy != null)
        {
            startActivity(intent_to_start_my_old_boy);
        }
    }

    /**
     * function: invokeGamePadTestApp
     * This method gets call when the GamepadTest button gets press. We create an intent to start
     * the GamepadTest app.
     */
    public void
    invokeGamePadTestApp(View view)
    {
        PackageManager packet_manager = getPackageManager();
        Intent intent_to_start_gamepad_test = packet_manager.getLaunchIntentForPackage(GAMEPAD_TESTER_PKG_NAME);

        if (intent_to_start_gamepad_test != null) {
            startActivity(intent_to_start_gamepad_test);
        }
    }

    /**
     * function: invokeSmartBoySerialApp
     * This method gets call when the SmartBoySerial button gets press. We create an intent to start
     * the SmartBoySerial App.
     */
    public void
    invokeSmartBoySerialApp(View view)
    {
        PackageManager packet_manager = getPackageManager();
        Intent intent_to_start_smartboy_serial = packet_manager.getLaunchIntentForPackage(SMART_BOY_SERIAL_PKG_NAME);

        if (intent_to_start_smartboy_serial != null)
        {
            startActivity(intent_to_start_smartboy_serial);
        }
    }

    /**
     * function: handleKeyDown_Controller
     * This method handles the key event (button press) from a game pad or joystick. For testing
     * purpose, the keycode and button of the Smart Boy gets display in the text box.
     * @param key_code      key code of the button press
     * @param key_event     key event of the button press (contains extra info such as device source, device id, etc)
     */
    private void
    handleKeyDown_Controller(int key_code, KeyEvent key_event)
    {
        if (previous_button_press != key_code) {
            switch (key_code) {
                case KeyEvent.KEYCODE_DPAD_UP:
                    button_presses_view.append("Pressed DPAD_UP Key code: " + key_code + "\n");
                    break;
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    button_presses_view.append("Pressed DPAD_LEFT Key code: " + key_code + "\n");
                    break;
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    button_presses_view.append("Pressed DPAD_RIGHT Key code: " + key_code + "\n");
                    break;
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    button_presses_view.append("Pressed DPAD_DOWN Key code: " + key_code + "\n");
                    break;
                case KeyEvent.KEYCODE_BUTTON_2:     // B button
                    button_presses_view.setText("Pressed B Key code: " + key_code + "\n");
                    break;
                case KeyEvent.KEYCODE_BUTTON_1:     // A button
                    button_presses_view.setText("Pressed A Key code: " + key_code + "\n");
                    break;
                case KeyEvent.KEYCODE_BUTTON_3:     // select
                    button_presses_view.setText("Pressed Select Key code: " + key_code + "\n");
                    break;
                case KeyEvent.KEYCODE_BUTTON_4:     // start
                    button_presses_view.setText("Pressed Start Key code: " + key_code + "\n");
                    break;
                case KeyEvent.KEYCODE_BUTTON_5:     // L button
                    button_presses_view.setText("Pressed L button Key code: " + key_code + "\n");
                    break;
                case KeyEvent.KEYCODE_BUTTON_6:     // R button
                    button_presses_view.setText("Pressed R button Key code: " + key_code + "\n");
                    break;
                default:
                    // anything else
                    if (key_code != KeyEvent.KEYCODE_DPAD_CENTER)
                    {
                        button_presses_view.setText("Pressed Unknown Key code: " + key_code + "\n");
                    }
                    break;
            }
        }

        previous_button_press = key_code;
    }

    /**
     * function: handleKeyUp_Controller
     * This method handles the key event (button press) from a game pad or joystick. For testing
     * purpose, the keycode and button of the Smart Boy gets display in the text box.
     * @param key_code      key code of the button press
     * @param key_event     key event of the button press (contains extra info such as device source, device id, etc)
     */
    private void
    handleKeyUp_Controller(int key_code, KeyEvent key_event)
    {
        switch (key_code)
        {
            case KeyEvent.KEYCODE_DPAD_UP:
                button_presses_view.append("Released DPAD_UP Key code: " + key_code + "\n");
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                button_presses_view.append("Released DPAD_LEFT Key code: " + key_code + "\n");
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                button_presses_view.append("Released DPAD_RIGHT Key code: " + key_code + "\n");
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                button_presses_view.append("Released DPAD_DOWN Key code: " + key_code + "\n");
                break;
            case KeyEvent.KEYCODE_BUTTON_2:     // B button
                button_presses_view.append("Released B Key code: " + key_code + "\n");
                break;
            case KeyEvent.KEYCODE_BUTTON_1:     // A button
                button_presses_view.append("Released A Key code: " + key_code + "\n");
                break;
            case KeyEvent.KEYCODE_BUTTON_3:     // select
                button_presses_view.append("Released Select Key code: " + key_code + "\n");
                break;
            case KeyEvent.KEYCODE_BUTTON_4:     // start
                button_presses_view.append("Released Start Key code: " + key_code + "\n");
                break;
            case KeyEvent.KEYCODE_BUTTON_5:     // L button
                button_presses_view.setText("Released L button Key code: " + key_code + "\n");
                break;
            case KeyEvent.KEYCODE_BUTTON_6:     // R button
                button_presses_view.setText("Released R button Key code: " + key_code + "\n");
                break;
            default:
                // anything else
                if (key_code != KeyEvent.KEYCODE_DPAD_CENTER)
                {
                    button_presses_view.append("Released Unknown Key code: " + key_code + "\n");
                }
                break;
        }

        // on button release clear out the text view for next button press
        previous_button_press = -1;
        //button_presses_view.setText("");
    }


    /**
     * function: processMotionEvent
     * This method takes in a motion event and break it down into components such as x, y, and z axis,
     * rotation x, y and z, Left trigger axis, Right trigger axis, hat x and hat y.
     * @param motion_event
     */
    public void processMotionEvent(MotionEvent motion_event)
    {
        String motion_event_details = "x = " + motion_event.getAxisValue(motion_event.AXIS_X) +
                                      "\ny = " + motion_event.getAxisValue(motion_event.AXIS_Y) +
                                      "\nz = " + motion_event.getAxisValue(motion_event.AXIS_Z) +
                                      "\nRx = " + motion_event.getAxisValue(motion_event.AXIS_RX) +
                                      "\nRy = " + motion_event.getAxisValue(motion_event.AXIS_RY) +
                                      "\nRz = " + motion_event.getAxisValue(motion_event.AXIS_RZ) +
                                      "\nL Trigger = " + motion_event.getAxisValue(motion_event.AXIS_LTRIGGER) +
                                      "\nR Trigger = " + motion_event.getAxisValue(motion_event.AXIS_RTRIGGER) +
                                      "\nHat X = " + motion_event.getAxisValue(motion_event.AXIS_HAT_X) +
                                      "\nHat Y = " + motion_event.getAxisValue(motion_event.AXIS_HAT_Y) + "\n";

        button_presses_view.append(motion_event_details);
    }

    /*******************************************************
     * InputDeviceListener abstract functions implementation
     *******************************************************/

    /**
     * function: onInputDeviceAdded
     * This method gets invoke when the input device listener sees a new device added.
     * Checks the device to see if it is a gamepad or joystick.
     * @param device_id     ID of the newly added device
     */
    @Override
    public void
    onInputDeviceAdded(int device_id)
    {
        InputDevice device = input_manager.getInputDevice(device_id);
        int device_type_sources = device.getSources();

        if (deviceIsGamePad(device_type_sources) || deviceIsJoystick(device_type_sources))
        {
            if (!controller_ids.contains(device_id)) {
                controller_ids.add(device_id);
            }
        }
    }

    /**
     * function: onInputDeviceChanged
     * This method gets invoke when an input device's properties changed.
     * Checks the device to see if it is a gamepad or joystick.
     * @param device_id     ID of the newly added device
     */
    @Override
    public void
    onInputDeviceChanged(int device_id)
    {
        InputDevice device = input_manager.getInputDevice(device_id);
        int device_type_sources = device.getSources();

        if (deviceIsGamePad(device_type_sources) || deviceIsJoystick(device_type_sources))
        {
            if (!controller_ids.contains(device_id)) {
                controller_ids.add(device_id);
            }
        }
    }

    /**
     * function: onInputDeviceRemoved
     * This method gets invoke when an input device gets removed. Checks the device
     * to see if it is a gamepad or joystick.
     * @param device_id     ID of the newly added device
     */
    @Override
    public void
    onInputDeviceRemoved(int device_id)
    {
        InputDevice device = input_manager.getInputDevice(device_id);
        int device_type_sources = device.getSources();

        if (deviceIsGamePad(device_type_sources) || deviceIsJoystick(device_type_sources))
        {
            if (controller_ids.contains(device_id)) {
                controller_ids.removeElement(device_id);
            }
        }
    }

    /**
     * function: onKeyDown
     * This method gets trigger when the input device listener sees an input press from an input device.
     * @param key_code      key code of the button press from any device
     * @param key_event     key event of the button press (contains extra info such as device source, device id, etc)
     * @return
     */
    @Override
    public boolean
    onKeyDown(int key_code, KeyEvent key_event)
    {
        if (controller_ids.contains(key_event.getDeviceId()))
        {
            handleKeyDown_Controller(key_code, key_event);
        }

        return super.onKeyDown(key_code, key_event);
    }

    /**
     * function: onKeyUp
     * THis method gets trigger when the input device listener sees an input release from an input device.
     * @param key_code      key code of the button press from any device
     * @param key_event     key event of the button press (contains extra info such as device source, device id, etc)
     */
    @Override
    public boolean
    onKeyUp(int key_code, KeyEvent key_event)
    {
        if (controller_ids.contains(key_event.getDeviceId()))
        {
            handleKeyUp_Controller(key_code, key_event);
        }

        return super.onKeyUp(key_code, key_event);
    }

    /**
     * function: onGenericMotionEvent
     * This method processes Motion Event trigger from the D-pad. All of the axises gets extract
     * from the motion event and gets nicely output on the text view.
     * @param motion_event  container for the D-pad event
     */
    @Override
    public boolean
    onGenericMotionEvent(MotionEvent motion_event)
    {
        button_presses_view.setText("");
        //button_presses_view.append("Motion Event Occurred!\n");
        processMotionEvent(motion_event);
        return super.onGenericMotionEvent(motion_event);
    }
}