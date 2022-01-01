
SELECT USER_ID, SUM(COUPONS.DISCOUNT_AMOUNT) AS "할인 금액" 
FROM CARTS JOIN COUPONS 
ON CARTS.ID = COUPONS.CART_ID 
GROUP BY CARTS.USER_ID
ORDER BY USER_ID