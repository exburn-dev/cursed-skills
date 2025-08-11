package com.jujutsu.client.keybind;

import com.jujutsu.event.client.KeyEvents;
import com.jujutsu.network.payload.AdditionalInputPressedPayload;
import com.jujutsu.systems.ability.AbilityAdditionalInput;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class AdditionalInputSystem {
    private static final List<AbilityAdditionalInput> stack = new ArrayList<>();
    private static final List<Pair<Boolean, Boolean>> stackStatus = new ArrayList<>();

    public static void register() {
        KeyEvents.KEY_RELEASED_EVENT.register(((client, keyCode, scanCode) -> {
            if(client.currentScreen != null || isEmpty()) return ActionResult.PASS;

            AbilityAdditionalInput input = stack.getLast();
            boolean status = stackStatus.getLast().getLeft();

            if(status) return ActionResult.PASS;

            if(input.keyCode() == keyCode) {
                Pair<Boolean, Boolean> pair = stackStatus.getLast();
                pair.setLeft(true);
                if((pair.getLeft() && pair.getRight()) || (pair.getLeft() && input.mouseButton() < 0) ) {
                    sendAdditionalInputPressed();
                }
            }

            return ActionResult.PASS;
        }));

        KeyEvents.MOUSE_BUTTON_RELEASED_EVENT.register(((client, button) -> {
            if(client.currentScreen != null || isEmpty()) return ActionResult.PASS;

            AbilityAdditionalInput input = stack.getLast();
            boolean status = stackStatus.getLast().getRight();

            if(status) return ActionResult.PASS;

            if(input.mouseButton() == button) {
                Pair<Boolean, Boolean> pair = stackStatus.getLast();
                pair.setRight(true);
                if((pair.getLeft() && pair.getRight()) || (pair.getRight() && input.keyCode() < 0) ) {
                    sendAdditionalInputPressed();
                }
            }

            return ActionResult.PASS;
        }));
    }

    private static void sendAdditionalInputPressed() {
        ClientPlayNetworking.send(new AdditionalInputPressedPayload(stack.getLast()));

        stack.removeLast();
        stackStatus.removeLast();
    }

    public static void addAdditionalInput(AbilityAdditionalInput additionalInput) {
        stack.add(additionalInput);
        stackStatus.add(new Pair<>(false, false));
    }

    public static boolean isEmpty() {
        return stack.isEmpty();
    }
}
