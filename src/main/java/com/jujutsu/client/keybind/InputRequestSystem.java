package com.jujutsu.client.keybind;

import com.jujutsu.event.client.KeyEvents;
import com.jujutsu.network.payload.input_requests.RequestedInputPressedC2SPayload;
import com.jujutsu.systems.ability.core.AbilitySlot;
import com.jujutsu.systems.ability.data.RequestedInputKey;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.util.ActionResult;

import java.util.*;

public class InputRequestSystem {
    private static final Map<AbilitySlot, RequestedInputKey> requests = new HashMap<>();

    public static void register() {
        KeyEvents.KEY_RELEASED_EVENT.register(((client, keyCode, scanCode) -> {
            if(client.currentScreen != null || isEmpty()) return ActionResult.PASS;

            processInput(true, keyCode);

            return ActionResult.PASS;
        }));

        KeyEvents.MOUSE_BUTTON_RELEASED_EVENT.register(((client, button) -> {
            if(client.currentScreen != null || isEmpty()) return ActionResult.PASS;

            processInput(false, button);

            return ActionResult.PASS;
        }));
    }

    private static void processInput(boolean isKeyboard, int key) {
        for(AbilitySlot slot: requests.keySet()) {
            RequestedInputKey requestedKey = requests.get(slot);
            boolean correctInput = isKeyboard ? comparePressedKey(requestedKey, key) : compareMouseButton(requestedKey, key);

            if(correctInput) {
                sendAdditionalInputPressed(slot);
                return;
            }
        }
    }

    private static boolean comparePressedKey(RequestedInputKey requestedKey, int keyCode) {
        return requestedKey.keyCode() == keyCode;
    }

    private static boolean compareMouseButton(RequestedInputKey requestedKey, int mouseButton) {
        return requestedKey.mouseButton() == mouseButton;
    }

    private static void sendAdditionalInputPressed(AbilitySlot slot) {
        if(!requests.containsKey(slot)) return;

        ClientPlayNetworking.send(new RequestedInputPressedC2SPayload(slot));

        requests.remove(slot);
    }

    public static void addInputRequest(AbilitySlot slot, RequestedInputKey input) {
        requests.put(slot, input);
    }

    public static void clearSlot(AbilitySlot slot) {
        requests.remove(slot);
    }

    public static Collection<RequestedInputKey> getAdditionalInput() {
        return requests.values();
    }

    public static boolean isEmpty() {
        return requests.isEmpty();
    }
}
