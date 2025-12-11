package com.jujutsu.client.keybind;

import com.jujutsu.event.client.KeyEvents;
import com.jujutsu.network.payload.AdditionalInputPressedPayload;
import com.jujutsu.systems.ability.core.AbilitySlot;
import com.jujutsu.systems.ability.data.RequestedInputKey;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.util.ActionResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AdditionalInputSystem {
    private static final Map<AbilitySlot, List<InputData>> stack = new HashMap<>();

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
        AbilitySlot slotToSend = null;
        InputData dataToSend = null;

        for(AbilitySlot slot: stack.keySet()) {
            for(InputData data: stack.get(slot)) {
                if(isKeyboard) {
                    if (processKeyPress(data, key, -1)) {
                        slotToSend = slot;
                        dataToSend = data;
                        break;
                    }
                }
                else {
                    if (processMousePress(data, key)) {
                        slotToSend = slot;
                        dataToSend = data;
                        break;
                    }
                }
            }
        }

        if(slotToSend != null) {
            sendAdditionalInputPressed(slotToSend, dataToSend);
        }
    }

    private static boolean processKeyPress(InputData inputData, int keyCode, int scanCode) {
        if(inputData.keyPressed) return false;

        if(inputData.input.keyCode() == keyCode) {
            inputData.keyPressed = true;

            return checkInputDataStatus(inputData);
        }

        return false;
    }

    private static boolean processMousePress(InputData inputData, int mouseButton) {
        if(inputData.mouseClicked) return false;

        if(inputData.input.mouseButton() == mouseButton) {
            inputData.mouseClicked = true;

            return checkInputDataStatus(inputData);
        }

        return false;
    }

    private static boolean checkInputDataStatus(InputData inputData) {
        return inputData.keyPressed && inputData.mouseClicked;
    }

    private static void sendAdditionalInputPressed(AbilitySlot slot, InputData data) {
        if(!stack.containsKey(slot)) return;

        ClientPlayNetworking.send(new AdditionalInputPressedPayload(data.input));

        stack.get(slot).remove(data);
        if(stack.get(slot).isEmpty()) {
            stack.remove(slot);
        }
    }

    public static void addAdditionalInput(AbilitySlot slot, RequestedInputKey input) {
        InputData data = new InputData(input);
        data.keyPressed = input.keyCode() < 0;
        data.mouseClicked = input.mouseButton() < 0;

        List<InputData> list;
        if(stack.containsKey(slot)) {
            list = stack.get(slot);
        }
        else {
            list = new ArrayList<>();
        }
        list.add(data);
        stack.put(slot, list);
    }

    public static void clearSlot(AbilitySlot slot) {
        if(stack.containsKey(slot)) {
            stack.get(slot).clear();
            stack.remove(slot);
        }
    }

    public static List<InputData> getAdditionalInput() {
        return stack.values().stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    public static boolean isEmpty() {
        return stack.isEmpty();
    }

    public static class InputData {
        public final RequestedInputKey input;
        private boolean keyPressed;
        private boolean mouseClicked;

        private InputData(RequestedInputKey input) {
            this.input = input;
        }
    }
}
