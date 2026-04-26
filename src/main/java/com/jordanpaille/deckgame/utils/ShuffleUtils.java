package com.jordanpaille.deckgame.utils;

import com.jordanpaille.deckgame.dto.Card;

import java.util.*;


public class ShuffleUtils {
    private static final Random random = new Random();

     // Yates-Fisher algorithm was used here
    public static void shuffle(List<Card> cards) {
        for (int i = cards.size() - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            Collections.swap(cards, i, j);
        }
    }
}
