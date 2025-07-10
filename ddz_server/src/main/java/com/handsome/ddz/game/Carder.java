package com.handsome.ddz.game;

import lombok.Data;

import java.util.*;
import java.util.stream.Collectors;

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

    public boolean isOneCard(List<Card> cardList) {
        if (cardList == null || cardList.isEmpty()) {
            return false;
        }
        return cardList.size() == 1;
    }

    //是否对子
    public boolean isDoubleCard (List<Card> cardList){
        if (cardList == null || cardList.isEmpty()) {
            return false;
        }
        if(cardList.size() != 2){
            return false;
        }
        //cardList[0].value==undefined说明是大小王，值是存储在king字段
        return cardList.get(0).getValue() != null
                && Objects.equals(cardList.get(1).getValue(), cardList.get(0).getValue());
    }

    //三张不带
    public boolean isThreeCard (List<Card> cardList){
        if(cardList.size() != 3){
            return false;
        }
        //不能是大小王
        boolean hasKing = cardList.stream().anyMatch(card -> card.getKing() != null);
        if (hasKing) {
            return false;
        }
        //判断三张牌是否相等
        long count = cardList.stream().map(Card::getValue).distinct().count();
        return count == 1;
    }

    //三带一
    public boolean isThreeAndOne(List<Card> cardList) {
        if(cardList.size() != 4){
            return false;
        }
        List<Card> numCards = cardList.stream().filter(card -> card.getValue() != null).collect(Collectors.toList());
        if (numCards.size() < 3) {
            return false;
        }
        Map<Integer, List<Card>> cardMap = numCards.stream().collect(Collectors.groupingBy(Card::getValue));
        for (Integer key : cardMap.keySet()) {
            List<Card> value = cardMap.get(key);
            if (value.size() == 3) {
                return true;
            }
        }
        return false;
    }

    public boolean isThreeAndTwo(List<Card> cardList) {
        if(cardList.size() != 5){
            return false;
        }
        List<Card> numCards = cardList.stream().filter(card -> card.getValue() != null).collect(Collectors.toList());
        if (numCards.size() < 5) {
            return false;
        }
        Map<Integer, List<Card>> cardMap = numCards.stream().collect(Collectors.groupingBy(Card::getValue));
        if (cardMap.size() != 2) {
            return false;
        }
        List<List<Card>> values = new ArrayList<>(cardMap.values());
        List<Card> cards0 = values.get(0);
        List<Card> cards1 = values.get(1);
        if (cards0.size() == 3 && cards1.size() == 2 || cards0.size() == 2 && cards1.size() == 3) {
            return true;
        }
        return false;
    }

    public boolean isBoom(List<Card> cardList) {
        if (cardList.size() != 4) {
            return false;
        }
        long count = cardList.stream().map(Card::getValue).distinct().count();
        return count == 1;
    }

    public boolean isKingBoom(List<Card> cardList) {
        if (cardList.size() != 2) {
            return false;
        }
        Integer king0 = cardList.get(0).getKing();
        Integer king1 = cardList.get(1).getKing();
        return king0 == 14 && king1 == 15 || king0 == 15 && king1 == 14;
    }

    public boolean isPlan(List<Card> cardList) {
        if (cardList.size() != 6) {
            return false;
        }
        List<Card> numCards = cardList.stream().filter(card -> card.getValue() != null).collect(Collectors.toList());
        if (numCards.size() < 6) {
            return false;
        }
        Map<Integer, List<Card>> cardMap = numCards.stream().collect(Collectors.groupingBy(Card::getValue));
        if (cardMap.size() != 2) {
            return false;
        }
        List<List<Card>> values = new ArrayList<>(cardMap.values());
        if (values.get(0).size() != 3 || values.get(1).size() != 3) {
            return false;
        }
        Integer value0 = values.get(0).get(0).getValue();
        Integer value1 = values.get(1).get(0).getValue();
        return Math.abs(value0 - value1) == 1
                && (value0 != 12 && value1 != 12);
    }

    //飞机带2单
    public boolean isPlanWithSingle(List<Card> cardList) {
        if (cardList.size() != 8) {
            return false;
        }
        List<Card> numCards = cardList.stream().filter(card -> card.getValue() != null).collect(Collectors.toList());
        if (numCards.size() < 6) {
            return false;
        }
        Map<Integer, List<Card>> valueMap = numCards.stream().collect(Collectors.groupingBy(Card::getValue));

        int[] nums = new int[2];
        int threeCount = 0;
        for (Integer value : valueMap.keySet()) {
            List<Card> cards = valueMap.get(value);
            if (cards.size() == 3) {
                nums[0] = value;
                threeCount ++;
            }
        }
        if (threeCount != 2) {
            return false;
        }
        List<Card> collect = numCards.stream().filter(card -> card.getValue() == nums[0] || card.getValue() == nums[1]).collect(Collectors.toList());
        return this.isPlan(collect);
    }


    public Object isCanPushs(Integer data) {
        return null;
    }
}
