package com.handsome.ddz.game;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

@Data
public class Carder {

    private Map<String,Integer> cardValue = new HashMap<>();;

    private Map<String,Integer> cardShape = new HashMap<>();

    private Map<String,Integer> kings = new HashMap<>();

    private List<Card> cardList = new ArrayList<>();

    public Carder() {
        cardValue.put("A", 12);
        cardValue.put("2", 13);
        cardValue.put("3", 1);
        cardValue.put("4", 2);
        cardValue.put("5", 3);
        cardValue.put("6", 4);
        cardValue.put("7", 5);
        cardValue.put("8", 6);
        cardValue.put("9", 7);
        cardValue.put("10", 8);
        cardValue.put("J", 9);
        cardValue.put("Q", 10);
        cardValue.put("K", 11);

        // 黑桃：spade 红桃：heart 梅花：club 方片：diamond
        cardShape.put("S", 1);
        cardShape.put("H", 2);
        cardShape.put("C", 3);
        cardShape.put("D", 4);

        kings.put("Kx", 14);
        kings.put("Kd", 15);

        initCardList();
        shuffle();

    }

    @Getter
    @AllArgsConstructor
    public enum CardType{
        ONE(1, "one"),
        DOUBLE(1, "double"),
        THREE(1, "three"),
        BOOM(2, "boom"),
        THREE_WITH_ONE(1, "threeWithOne"),
        THREE_WITH_TWO(1, "threeWithTwo"),
        PLANE(1, "plane"),
        PLANE_WITH_SINGLE(1, "planeWithSingle"),
        PLANE_WITH_TWO(1, "planeWithTwo"),
        STRAIGHT(1, "straight"),
        DOUBLE_SCROLL(1, "doubleScroll"),
        KING_BOOM(3, "kingboom"),
        NOT_SUPPORT(0, "notSupport"),
        ;
        private int level;
        private String type;
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

    //飞机
    public boolean isPlane(List<Card> cardList) {
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
    public boolean isPlaneWithSingle(List<Card> cardList) {
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
        return this.isPlane(collect);
    }

    //飞机带2对
    public boolean isPlaneWithDouble(List<Card> cardList){
        if (cardList.size() != 10) {
            return false;
        }
        List<Card> numCards = cardList.stream().filter(card -> card.getValue() != null).collect(Collectors.toList());
        if (numCards.size() < 10) {
            return false;
        }
        Map<Integer, List<Card>> valueMap = numCards.stream().collect(Collectors.groupingBy(Card::getValue));

        int[] nums = new int[2];
        int threeCount = 0;
        int twoCount = 0;
        for (Integer value : valueMap.keySet()) {
            List<Card> cards = valueMap.get(value);
            if (cards.size() == 3) {
                nums[0] = value;
                threeCount ++;
            }
            if (cards.size() == 2) {
                twoCount ++;
            }
        }
        if (threeCount != 2) {
            return false;
        }
        if (twoCount != 2) {
            return false;
        }

        List<Card> collect = numCards.stream().filter(card -> card.getValue() == nums[0] || card.getValue() == nums[1]).collect(Collectors.toList());
        return this.isPlane(collect);
    }

    //顺子
    public boolean isStraight(List<Card> cardList) {
        if (cardList.size() < 5 || cardList.size() > 12) {
            return false;
        }
        List<Card> kingCards = cardList.stream().filter(card -> card.getValue() == null).collect(Collectors.toList());
        if (!kingCards.isEmpty()) {
            return false;
        }
        List<Card> collect = cardList.stream().sorted(Comparator.comparingInt(Card::getValue)).collect(Collectors.toList());
        for (int i = 0; i < collect.size() - 1; i++) {
            if (collect.get(i).getValue() + 1 != collect.get(i + 1).getValue()) {
                return false;
            }
        }
        return true;
    }

    //连对
    public boolean isDoubleScroll(List<Card> cardList) {
        if (cardList.size() < 6) {
            return false;
        }
        if (cardList.size() % 2 != 0) {
            return false;
        }
        //判断是不是连对
        List<Card> kingCards = cardList.stream().filter(card -> card.getValue() == null).collect(Collectors.toList());
        if (!kingCards.isEmpty()) {
            return false;
        }
        List<Card> collect = cardList.stream().sorted(Comparator.comparingInt(Card::getValue)).collect(Collectors.toList());
        for (int i = 0; i < collect.size() - 3; i += 2) {
            if (collect.get(i).getValue() + 1 != collect.get(i + 2).getValue()) {
                return false;
            }
            if (collect.get(i).getValue() != collect.get(i + 1).getValue()) {
                return false;
            }
        }
        return true;
    }

    public CardType getCardType(List<Card> cardList) {
        return this.isCanPushs(cardList);
    }

    public CardType isCanPushs(List<Card> pushCardList) {
        if (isOneCard(pushCardList)) {
            return CardType.ONE;
        }

        if(isDoubleCard(pushCardList)){
            return CardType.DOUBLE;
        }

        if(isThreeCard(pushCardList)){
            return CardType.THREE;
        }

        if(isThreeAndOne(pushCardList)){
            return CardType.THREE_WITH_ONE;
        }

        if(isThreeAndTwo(pushCardList)){
            return CardType.THREE_WITH_TWO;
        }

        if(isBoom(pushCardList)){
            return CardType.BOOM;
        }

        if(isKingBoom(pushCardList)){
            return CardType.KING_BOOM;
        }

        if(isPlane(pushCardList)){
            return CardType.PLANE;
        }

        if(isPlaneWithSingle(pushCardList)){
            return CardType.PLANE_WITH_SINGLE;
        }

        if(isPlaneWithDouble(pushCardList)){
            return CardType.PLANE_WITH_TWO;
        }

        if(isStraight(pushCardList)){
            return CardType.STRAIGHT;
        }

        if(isDoubleScroll(pushCardList)){
            return CardType.DOUBLE_SCROLL;
        }
        //return false
        return CardType.NOT_SUPPORT;
    }

    public boolean compareWithCard(List<Card> lastPushCardList, List<Card> pushCardList){
        if (lastPushCardList.size() != pushCardList.size()) {
            return false;
        }
        CardType lastCardType = getCardType(lastPushCardList);
        CardType curCardType = getCardType(pushCardList);
        if (curCardType != lastCardType) {
            return false;
        } else {
            return compare(lastPushCardList, pushCardList, lastCardType);
        }
    }

    private boolean compare(List<Card> cardA, List<Card> cardB, CardType cardType) {
        boolean result;
        switch(cardType){
            case ONE:
                result = compareOne(cardA,cardB);
                break;
            case DOUBLE:
                result = compareDouble(cardA,cardB);
                break;
            case THREE:
                result = compareThree(cardA,cardB);
                break;
            case BOOM:
                result = compareBoom(cardA,cardB);
                break;
            case KING_BOOM:
                result = compareBoomKing(cardA,cardB);
                break;
            case PLANE_WITH_SINGLE:
                result = comparePlanWithSing(cardA,cardB);
                break;
            case PLANE_WITH_TWO:
                result = comparePlanWithTwo(cardA,cardB);
                break;
            case PLANE:
                result = comparePlane(cardA,cardB);
                break;
            case STRAIGHT:
                result = compareScroll(cardA,cardB);
                break;
            case DOUBLE_SCROLL:
                result = compareDoubleScroll(cardA,cardB);
                break;
            default:
                result = false;
                break;
        }

        return result;
    }

    private boolean compareDoubleScroll(List<Card> cardA, List<Card> cardB) {
        return false;
    }

    private boolean compareScroll(List<Card> cardA, List<Card> cardB) {
        return false;
    }

    private boolean comparePlane(List<Card> cardA, List<Card> cardB) {
        return false;
    }

    private boolean comparePlanWithTwo(List<Card> cardA, List<Card> cardB) {
        return false;
    }

    private boolean comparePlanWithSing(List<Card> cardA, List<Card> cardB) {
        return false;
    }

    private boolean compareBoomKing(List<Card> cardA, List<Card> cardB) {
        return false;
    }

    private boolean compareBoom(List<Card> cardA, List<Card> cardB) {
        return false;
    }

    private boolean compareThree(List<Card> cardA, List<Card> cardB) {
        return false;
    }

    private boolean compareDouble(List<Card> cardA, List<Card> cardB) {
        return false;
    }

    private boolean compareOne(List<Card> cardA, List<Card> cardB) {
        return false;
    }
}
