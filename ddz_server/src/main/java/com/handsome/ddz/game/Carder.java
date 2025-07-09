package com.handsome.ddz.game;

import lombok.Data;

import java.util.*;

@Data
public class Carder {

    private Map<String,Integer> cardValue = new HashMap<>();;

    private Map<String,Integer> cardShape = new HashMap<>();

    private Map<String,Integer> kings = new HashMap<>();

    private List<Card> cardList = new ArrayList<>();

    public Carder() {
        cardValue.put("A",12);
        cardValue.put("2",13);
        cardValue.put("3",1);
        cardValue.put("4",2);
        cardValue.put("5",3);
        cardValue.put("6",4);
        cardValue.put("7",5);
        cardValue.put("8",6);
        cardValue.put("9",7);
        cardValue.put("10",8);
        cardValue.put("J",9);
        cardValue.put("Q",10);
        cardValue.put("K",11);

        // 黑桃：spade 红桃：heart 梅花：club 方片：diamond
        cardShape.put("S",1);
        cardShape.put("H",2);
        cardShape.put("C",3);
        cardShape.put("D",4);

        kings.put("Kx",14);
        kings.put("Kd",15);

        initCardList();
        shuffle();
    }

    public void initCardList() {
        for (int value : cardValue.values()) {
            for (int shape : cardShape.values()) {
                Card card = new Card(value, shape, null);
                card.setIndex(cardList.size());
                cardList.add(card);
            }
        }

        for (Integer value : kings.values()) {
            Card card = new Card(null, null, value);
            card.setIndex(cardList.size());
            cardList.add(card);
        }
    }

    private void shuffle() {
        // 获取当前时间戳
        Collections.shuffle(cardList);
    }

    public List<List<Card>> splitThreeCards() {
        // 参数校验：牌列表至少需要54张（17 * 3 + 3）
        if (cardList.size() < 54) {
            throw new IllegalArgumentException("牌列表长度必须至少为54，当前长度：" + cardList.size());
        }

        // 初始化一个Map，用于存储三份牌（键0、1、2分别对应三份）
        Map<Integer, List<Card>> threeCards = new HashMap<>();

        // 循环17次，每次为三份牌各添加一张牌
        for (int i = 0; i < 17; i++) {
            // 内层循环3次，为三份牌依次添加当前牌
            for (int j = 0; j < 3; j++) {
                // 从牌列表末尾移除一张牌（模拟JavaScript的pop操作）
                Card currentCard = cardList.remove(cardList.size() - 1);
                // 如果当前份（j）不存在于Map中，创建一个新的空列表；否则直接获取已有列表
                threeCards.computeIfAbsent(j, k -> new ArrayList<>()).add(currentCard);
            }
        }

        // 构建结果列表：前三个元素是三份17张的牌，第四个元素是剩余的3张牌
        List<List<Card>> result = new ArrayList<>();
        result.add(threeCards.get(0));   // 第一份牌（17张）
        result.add(threeCards.get(1));   // 第二份牌（17张）
        result.add(threeCards.get(2));   // 第三份牌（17张）
        result.add(cardList);            // 剩余的3张牌（带翻）

        return result;
    }

    public Object isCanPushs(Integer data) {
        return null;
    }
}
