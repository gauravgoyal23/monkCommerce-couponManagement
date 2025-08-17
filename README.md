# monkCommerce-couponManagement
# E-Commerce Coupon Management System

A comprehensive RESTful API for managing and applying different types of discount coupons for an e-commerce platform.
The system supports cart-wise, product-wise, and Buy X Get Y (BxGy) coupons with extensible architecture for future
coupon types.

## 🚀 Features

### Implemented Coupon Types

1. Cart-wise Coupons
2. Product-wise Coupons
3. Buy X Get Y (BxGy) Coupons

### Core Functionality

- Complete CRUD operations for coupons
- Cart discount calculation and application
- Expiration date support
- Comprehensive error handling
- Configurable data loading

## 📋 API Endpoints

| Method | Endpoint                     | Description                		|
|--------|------------------------------|-----------------------------------|
| POST   | `/api/createCoupon` 			| Create a new coupon        		|
| GET    | `/api/getAllCoupons` 		| Retrieve all coupons       		|
| GET    | `/api/getCouponById/{id}` 	| Retrieve a specific coupon 		|
| PUT    | `/api/updateCouponById/{id}` | Update a specific coupon   		|
| DELETE | `/api/deleteCouponById/{id}` | Delete a specific coupon   		|
| POST   | `/api/applicable-coupons`	| Find applicable coupons for a cart|
| POST   | `/api/apply-coupon/{id}`		| Apply a specific coupon to cart   |


### Technology used

- Framework	: Spring Boot 2.7.18
- Database	: H2 in memory database
- ORM		: Spring Data JPA with Hibernate
- Build Tool: Maven


## Implemented Use Cases
NOTE: On startup project there are 4 coupons that are hardcoded saved in db and everytime when we start, older data will clean up.
1. Cart-wise Coupons

Implemented Cases:

- Percentage discounts (e.g., 10% off on cart total)
- Fixed amount discounts (e.g., $20 off on cart)
- Minimum cart value requirements
- Maximum discount amount limits
- Expiration date validation

Example:

{
"code": "CART10",
"name": "10% Off on Cart",
"description": "Get 10% off on cart total above $100",
"type": "CART_WISE",
"discountValue": 10,
"discountType": "PERCENTAGE",
"minimumCartValue": 100,
"maxDiscountAmount": 50
}

2. Product-wise Coupons

Implemented Cases:

- Percentage discounts on specific products
- Fixed amount discounts per product
- Multiple applicable products
- Usage limits and tracking

Example:

{
"code": "PROD20",
"name": "20% Off on Electronics",
"description": "Get 20% off on electronics products",
"type": "PRODUCT_WISE",
"discountValue": 20,
"discountType": "PERCENTAGE",
"applicableProductIds": [
1,
2,
3
],
"maxDiscountAmount": 100
}

3. Buy X Get Y (BxGy) Coupons

Implemented Cases:

- Buy X products, get Y products free
- Multiple buy-get rules with priorities
- Repetition limits
- Complex product combinations
- Rule-based discount calculation

Example:

{
"code": "B2G1",
"name": "Buy 2 Get 1 Free",
"description": "Buy 2 products from selected items, get 1 free",
"type": "BXGY",
"bxgyRules": [
{
"buyQuantity": 2,
"buyProductIds": [
1,
2,
3
],
"getQuantity": 1,
"getProductIds": [
4,
5
],
"priority": 1
}
],
"repetitionLimit": 3
}

## Unimplemented Cases

### Complex BxGy Scenarios Not Implemented:
- Buy X from category A AND Y from category B, get Z free
- Conditional BxGy rules (e.g., only if cart total > $200)
- Stacking multiple BxGy coupons
- Buy X get Y with partial quantities
- Complex cross-product dependencies

Reasons:

- Time constraints for complex rule engine
- Need for more validation logic

### Advanced Coupon Features

Not Implemented:
- User-specific coupons (first-time buyers, VIP customers)
- Time-based restrictions (specific hours, days)
- Coupon combination restrictions

Reasons:
- Requires user management system
- Complex validation logic
- Additional infrastructure requirements

### Performance Optimizations

Not Implemented:
- Caching for frequently accessed coupons
- Database indexing optimization
- Bulk coupon operations

Reasons:
- Focused on core functionality first
- Requires testing and monitoring

### Current Assumptions

1. Products are assumed to exist with IDs 1-8 for sample data (can be updated with update api or can create new coupon)
2. All amounts are in USD (no multi-currency support)
3. Generic error responses with appropriate HTTP status codes


## Getting Started

Server URL: http://localhost:8080/

# 1. GET: /api/getAllCoupons -> Retrieve all coupons
`http://localhost:8080/api/coupons`
`[
{
"id": 1,
"code": "CART10",
"name": "10% Off on Cart",
"description": "Get 10% off on cart total above $100",
"type": "CART_WISE",
"discountValue": 10,
"discountType": "PERCENTAGE",
"validFrom": "2025-08-16T20:26:46.196",
"validUntil": "2025-09-17T20:26:46.196",
"minimumCartValue": 100,
"maxUsage": 100,
"currentUsage": 0,
"maxDiscountAmount": 50,
"repetitionLimit": 1,
"createdAt": "2025-08-17T20:26:46.206",
"updatedAt": "2025-08-17T20:26:46.206",
"calculatedDiscount": null,
"discountDescription": null,
"active": true
},
{
"id": 2,
"code": "PROD20",
"name": "20% Off on Electronics",
"description": "Get 20% off on electronics products",
"type": "PRODUCT_WISE",
"discountValue": 20,
"discountType": "PERCENTAGE",
"validFrom": "2025-08-16T20:26:46.315",
"validUntil": "2025-09-17T20:26:46.315",
"minimumCartValue": null,
"maxUsage": 50,
"currentUsage": 0,
"maxDiscountAmount": 100,
"repetitionLimit": 1,
"createdAt": "2025-08-17T20:26:46.316",
"updatedAt": "2025-08-17T20:26:46.316",
"calculatedDiscount": null,
"discountDescription": null,
"active": true
},
{
"id": 3,
"code": "B2G1",
"name": "Buy 2 Get 1 Free",
"description": "Buy 2 products from selected items, get 1 free",
"type": "BXGY",
"discountValue": 0,
"discountType": "FIXED_AMOUNT",
"validFrom": "2025-08-16T20:26:46.358",
"validUntil": "2025-09-17T20:26:46.358",
"minimumCartValue": null,
"maxUsage": 30,
"currentUsage": 0,
"maxDiscountAmount": null,
"repetitionLimit": 3,
"createdAt": "2025-08-17T20:26:46.358",
"updatedAt": "2025-08-17T20:26:46.358",
"calculatedDiscount": null,
"discountDescription": null,
"active": true
},
{
"id": 4,
"code": "SAVE20",
"name": "Save $20",
"description": "Save $20 on orders above $150",
"type": "CART_WISE",
"discountValue": 20,
"discountType": "FIXED_AMOUNT",
"validFrom": "2025-08-16T20:26:46.377",
"validUntil": "2025-09-17T20:26:46.377",
"minimumCartValue": 150,
"maxUsage": 75,
"currentUsage": 0,
"maxDiscountAmount": 20,
"repetitionLimit": 1,
"createdAt": "2025-08-17T20:26:46.377",
"updatedAt": "2025-08-17T20:26:46.377",
"calculatedDiscount": null,
"discountDescription": null,
"active": true
}
]`

# 2. GET   : /api/getCouponById/{id} -> Retrieve a specific coupon
`http://localhost:8080/api/coupons/3`
`{
"id": 3,
"code": "B2G1",
"name": "Buy 2 Get 1 Free",
"description": "Buy 2 products from selected items, get 1 free",
"type": "BXGY",
"discountValue": 0.00,
"discountType": "FIXED_AMOUNT",
"validFrom": "2025-08-16T20:26:46.358",
"validUntil": "2025-09-17T20:26:46.358",
"minimumCartValue": null,
"maxUsage": 30,
"currentUsage": 0,
"maxDiscountAmount": null,
"repetitionLimit": 3,
"createdAt": "2025-08-17T20:26:46.358",
"updatedAt": "2025-08-17T20:26:46.358",
"calculatedDiscount": null,
"discountDescription": null,
"active": true
}`

# 3. POST  : /api/createCoupon -> Create a new coupon
`http://localhost:8080/api/createCoupon`
Request:
`{
"code": "B5G3",
"name": "Buy 5 Get 3 Free",
"description": "Buy 5 items from premium products, get 3 items free",
"type": "BXGY",
"discountValue": 0.00,
"discountType": "FIXED_AMOUNT",
"isActive": true,
"validFrom": "2025-08-16T00:00:00",
"validUntil": "2025-12-31T23:59:59",
"maxUsage": 25,
"repetitionLimit": 2,
"bxgyRules": [
{
"buyQuantity": 5,
"buyProductIds": [
10,
11,
12,
13,
14
],
"getQuantity": 3,
"getProductIds": [
15,
16,
17,
18,
19
],
"priority": 1
}
]
}`

Response:
`{
"id": 5,
"code": "B5G3",
"name": "Buy 5 Get 3 Free",
"description": "Buy 5 items from premium products, get 3 items free",
"type": "BXGY",
"discountValue": 0.00,
"discountType": "FIXED_AMOUNT",
"validFrom": "2025-08-16T00:00:00",
"validUntil": "2025-12-31T23:59:59",
"minimumCartValue": null,
"maxUsage": 25,
"currentUsage": 0,
"maxDiscountAmount": null,
"repetitionLimit": 2,
"createdAt": "2025-08-17T20:29:25.331",
"updatedAt": "2025-08-17T20:29:25.331",
"calculatedDiscount": null,
"discountDescription": null,
"active": true
}`

# 4. PUT : /api/updateCouponById/{id} -> Update a specific coupon
`http://localhost:8080/api/coupons/1`
Request:
`{
"id": 1,
"code": "CART25",
"name": "25% Off on Cart",
"description": "Get 25% off on cart total above $100",
"type": "CART_WISE",
"discountValue": 500.00,
"discountType": "PERCENTAGE",
"validFrom": "2025-08-16T20:26:46.196",
"validUntil": "2025-09-17T20:26:46.196",
"minimumCartValue": 3000.00,
"maxUsage": 50,
"currentUsage": 0,
"maxDiscountAmount": 400.00,
"repetitionLimit": 1,
"createdAt": "2025-08-17T20:26:46.206",
"updatedAt": "2025-08-17T20:26:46.206",
"calculatedDiscount": null,
"discountDescription": null,
"active": true
}`
Response:
`{
"id": 1,
"code": "CART25",
"name": "25% Off on Cart",
"description": "Get 25% off on cart total above $100",
"type": "CART_WISE",
"discountValue": 500.00,
"discountType": "PERCENTAGE",
"validFrom": "2025-08-16T20:26:46.196",
"validUntil": "2025-09-17T20:26:46.196",
"minimumCartValue": 3000.00,
"maxUsage": 50,
"currentUsage": 0,
"maxDiscountAmount": 400.00,
"repetitionLimit": 1,
"createdAt": "2025-08-17T20:26:46.206",
"updatedAt": "2025-08-17T20:34:51.797",
"calculatedDiscount": null,
"discountDescription": null,
"active": true
}`

# 5. DELETE: /api/deleteCouponById/{id} -> Delete a specific coupon
`http://localhost:8080/api/coupons/5`

Response:
`204No Content`

# 6. POST  : /api/applicable-coupons -> Find applicable coupons for a cart
`http://localhost:8080/api/applicable-coupons`
Request:
`{
"items": [
{
"productId": 1,
"productName": "Product 1",
"quantity": 4,
"price": 50
},
{
"productId": 4,
"productName": "Product 4",
"quantity": 2,
"price": 30
}
]
}`
Response:
`{
"applicableCoupons": [
{
"id": 3,
"code": "B2G1",
"name": "Buy 2 Get 1 Free",
"description": "Buy 2 products from selected items, get 1 free",
"type": "BXGY",
"discountValue": 0.00,
"discountType": "FIXED_AMOUNT",
"validFrom": "2025-08-16T20:26:46.358",
"validUntil": "2025-09-17T20:26:46.358",
"minimumCartValue": null,
"maxUsage": 30,
"currentUsage": 0,
"maxDiscountAmount": null,
"repetitionLimit": 3,
"createdAt": "2025-08-17T20:26:46.358",
"updatedAt": "2025-08-17T20:26:46.358",
"calculatedDiscount": 60.00,
"discountDescription": "Buy X Get Y discount applied",
"active": true
},
{
"id": 2,
"code": "PROD20",
"name": "20% Off on Electronics",
"description": "Get 20% off on electronics products",
"type": "PRODUCT_WISE",
"discountValue": 20.00,
"discountType": "PERCENTAGE",
"validFrom": "2025-08-16T20:26:46.315",
"validUntil": "2025-09-17T20:26:46.315",
"minimumCartValue": null,
"maxUsage": 50,
"currentUsage": 0,
"maxDiscountAmount": 100.00,
"repetitionLimit": 1,
"createdAt": "2025-08-17T20:26:46.316",
"updatedAt": "2025-08-17T20:26:46.316",
"calculatedDiscount": 40.00,
"discountDescription": "20.00% off on applicable products",
"active": true
},
{
"id": 4,
"code": "SAVE20",
"name": "Save $20",
"description": "Save $20 on orders above $150",
"type": "CART_WISE",
"discountValue": 20.00,
"discountType": "FIXED_AMOUNT",
"validFrom": "2025-08-16T20:26:46.377",
"validUntil": "2025-09-17T20:26:46.377",
"minimumCartValue": 150.00,
"maxUsage": 75,
"currentUsage": 0,
"maxDiscountAmount": 20.00,
"repetitionLimit": 1,
"createdAt": "2025-08-17T20:26:46.377",
"updatedAt": "2025-08-17T20:26:46.377",
"calculatedDiscount": 20.00,
"discountDescription": "$20.00 off on cart total of 260",
"active": true
}
],
"totalApplicableCoupons": 3
}
`
# 7. POST  : /api/apply-coupon/{id} -> Apply a specific coupon to cart
Request: 
`{
"items": [
{
"productId": 1,
"productName": "Product 1",
"quantity": 4,
"price": 50
},
{
"productId": 4,
"productName": "Product 4",
"quantity": 2,
"price": 30
}
]
}`
## Scenario 1: APPLYING COUPON ID 3: B2G1
`http://localhost:8080/api/apply-coupon/3`
`{
"id": null,
"items": [
{
"id": null,
"productId": 1,
"productName": "Product 1",
"quantity": 4,
"price": 50,
"discount": 0,
"totalDiscount": 0,
"finalPrice": 200
},
{
"id": null,
"productId": 4,
"productName": "Product 4",
"quantity": 2,
"price": 30,
"discount": 0,
"totalDiscount": 0,
"finalPrice": 60
}
],
"totalPrice": 260,
"totalDiscount": 60.00,
"finalPrice": 200.00,
"appliedCoupon": {
"id": 3,
"code": "B2G1",
"name": "Buy 2 Get 1 Free",
"description": "Buy 2 products from selected items, get 1 free",
"type": "BXGY",
"discountValue": 0.00,
"discountType": "FIXED_AMOUNT",
"validFrom": "2025-08-16T20:26:46.358",
"validUntil": "2025-09-17T20:26:46.358",
"minimumCartValue": null,
"maxUsage": 30,
"currentUsage": 1,
"maxDiscountAmount": null,
"repetitionLimit": 3,
"createdAt": "2025-08-17T20:26:46.358",
"updatedAt": "2025-08-17T20:39:29.766",
"calculatedDiscount": null,
"discountDescription": null,
"active": true
},
"createdAt": null,
"updatedAt": null
}
`
## Scenario 2: APPLYING COUPON ID 2: PROD20
`http://localhost:8080/api/apply-coupon/2`

`{
"id": null,
"items": [
{
"id": null,
"productId": 1,
"productName": "Product 1",
"quantity": 4,
"price": 50,
"discount": 0,
"totalDiscount": 0,
"finalPrice": 200
},
{
"id": null,
"productId": 4,
"productName": "Product 4",
"quantity": 2,
"price": 30,
"discount": 0,
"totalDiscount": 0,
"finalPrice": 60
}
],
"totalPrice": 260,
"totalDiscount": 60.00,
"finalPrice": 200.00,
"appliedCoupon": {
"id": 3,
"code": "B2G1",
"name": "Buy 2 Get 1 Free",
"description": "Buy 2 products from selected items, get 1 free",
"type": "BXGY",
"discountValue": 0.00,
"discountType": "FIXED_AMOUNT",
"validFrom": "2025-08-16T20:26:46.358",
"validUntil": "2025-09-17T20:26:46.358",
"minimumCartValue": null,
"maxUsage": 30,
"currentUsage": 1,
"maxDiscountAmount": null,
"repetitionLimit": 3,
"createdAt": "2025-08-17T20:26:46.358",
"updatedAt": "2025-08-17T20:39:29.766",
"calculatedDiscount": null,
"discountDescription": null,
"active": true
},
"createdAt": null,
"updatedAt": null
}
`
## Scenario 3: APPLY COUPON ID 4: SAVE20
`http://localhost:8080/api/apply-coupon/4`

`{
"id": null,
"items": [
{
"id": null,
"productId": 1,
"productName": "Product 1",
"quantity": 4,
"price": 50,
"discount": 0,
"totalDiscount": 0,
"finalPrice": 200
},
{
"id": null,
"productId": 4,
"productName": "Product 4",
"quantity": 2,
"price": 30,
"discount": 0,
"totalDiscount": 0,
"finalPrice": 60
}
],
"totalPrice": 260,
"totalDiscount": 20.00,
"finalPrice": 240.00,
"appliedCoupon": {
"id": 4,
"code": "SAVE20",
"name": "Save $20",
"description": "Save $20 on orders above $150",
"type": "CART_WISE",
"discountValue": 20.00,
"discountType": "FIXED_AMOUNT",
"validFrom": "2025-08-16T20:26:46.377",
"validUntil": "2025-09-17T20:26:46.377",
"minimumCartValue": 150.00,
"maxUsage": 75,
"currentUsage": 1,
"maxDiscountAmount": 20.00,
"repetitionLimit": 1,
"createdAt": "2025-08-17T20:26:46.377",
"updatedAt": "2025-08-17T20:42:28.858",
"calculatedDiscount": null,
"discountDescription": null,
"active": true
},
"createdAt": null,
"updatedAt": null
}`
