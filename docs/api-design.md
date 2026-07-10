# 房间模块接口文档

文档日期：2026年07月10日

本文档详细描述房间模块的所有接口，包括请求方式、请求头、请求参数、响应数据及失败场景说明。

## 1\. 创建房间

**请求方式**：/api/room/create
**请求头**：Authorization: Bearer \{token\}（token为用户登录凭证，必填）

### 请求参数（JSON格式）

```json
{
  "roomName": "周末开黑",
  "maxMembers": 10
}
```

**参数说明**：

- roomName：字符串，房间名称，必填

- maxMembers：数字，房间最大成员数，必填，需为正整数

### 响应数据（成功，JSON格式）

```json
{
  "code": 200,
  "message": "房间创建成功",
  "data": {
    "roomId": 1,
    "roomCode": "A3F9K2",
    "roomName": "周末开黑",
    "ownerId": 1,
    "ownerName": "张三",
    "maxMembers": 10,
    "currentMembers": 1,
    "status": 1,
    "createdAt": "2026-07-10 15:30:00"
  }
}
```

**响应参数说明**：

- code：响应状态码，200表示成功

- message：响应提示信息

- data：返回的房间详情数据

- roomId：房间唯一标识

- roomCode：房间邀请码，唯一

- ownerId：房主用户ID

- ownerName：房主用户名

- currentMembers：当前房间成员数

- status：房间状态（1表示正常，可根据实际业务补充其他状态值说明）

- createdAt：房间创建时间，格式为"YYYY\-MM\-DD HH:mm:ss"

## 2\. 加入房间

**请求方式**：POST

**请求路径**：/api/room/join

**请求头**：Authorization: Bearer \{token\}（token为用户登录凭证，必填）

### 请求参数（JSON格式）

```json
{
  "roomCode": "A3F9K2"
}
```

**参数说明**：

- roomCode：字符串，房间邀请码，必填

### 响应数据（成功，JSON格式）

```json
{
  "code": 200,
  "message": "加入成功",
  "data": {
    "roomId": 1,
    "roomCode": "A3F9K2",
    "roomName": "周末开黑",
    "ownerId": 1,
    "ownerName": "张三",
    "maxMembers": 10,
    "currentMembers": 2,
    "status": 1
  }
}
```

### 失败场景响应（JSON格式）

**场景1：房间不存在或已解散**

```json
{
  "code": 404,
  "message": "房间不存在或已解散",
  "data": null
}
```

**场景2：房间已满**

```json
{
  "code": 400,
  "message": "房间已满",
  "data": null
}
```

**场景3：你已在该房间中**

```json
{
  "code": 400,
  "message": "你已在该房间中",
  "data": null
}
```

## 3\. 查询房间信息

**请求方式**：GET

**请求路径**：/api/room/\{roomCode\}（\{roomCode\}为房间邀请码，需替换为实际值）

**请求头**：Authorization: Bearer \{token\}（token为用户登录凭证，必填）

### 响应数据（成功，JSON格式）

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "roomId": 1,
    "roomCode": "A3F9K2",
    "roomName": "周末开黑",
    "ownerId": 1,
    "ownerName": "张三",
    "maxMembers": 10,
    "currentMembers": 2,
    "status": 1,
    "members": [
      { "userId": 1, "username": "张三", "avatar": "/default-avatar.png", "role": 1 },
      { "userId": 2, "username": "李四", "avatar": "/default-avatar.png", "role": 0 }
    ]
  }
}
```

**响应参数补充说明**：

- members：房间成员列表，包含成员的用户ID、用户名、头像地址、角色

- role：成员角色（1表示房主，0表示普通成员，可根据实际业务补充其他角色值说明）

## 4\. 退出房间

**请求方式**：POST

**请求路径**：/api/room/leave

**请求头**：Authorization: Bearer \{token\}（token为用户登录凭证，必填）

### 请求参数（JSON格式）

```json
{
  "roomCode": "A3F9K2"
}
```

**参数说明**：

- roomCode：字符串，房间邀请码，必填

### 响应数据（成功，JSON格式）

**场景1：普通成员退出**

```json
{
  "code": 200,
  "message": "已退出房间",
  "data": null
}
```

**场景2：房主退出（触发房主转移）**

```json
{
  "code": 200,
  "message": "已退出房间",
  "data": {
    "transferred": true,
    "newOwnerId": 2,
    "newOwnerName": "李四"
  }
}
```

**场景2：房主退出（只有房主一个人时）**

```json
{
  "code": 200,
  "message": "已退出房间，房间已解散",
  "data": {
    "transferred": true,
    "newOwnerId": null,
    "newOwnerName": null
  }
}
```

**响应参数说明**：

- transferred：布尔值，是否触发房主转移

- newOwnerId：新房主用户ID

- newOwnerName：新房主用户名

## 5\. 解散房间

**请求方式**：POST

**请求路径**：/api/room/disband

**请求头**：Authorization: Bearer \{token\}（token为用户登录凭证，必填）

### 请求参数（JSON格式）

```json
{
  "roomCode": "A3F9K2"
}
```

**参数说明**：

- roomCode：字符串，房间邀请码，必填

### 响应数据（成功，JSON格式）

```json
{
  "code": 200,
  "message": "房间已解散",
  "data": null
}
```

### 失败场景响应（JSON格式）

**场景1：非房主尝试解散**

```json
{
  "code": 403,
  "message": "只有房主才能解散房间",
  "data": null
}
```

**场景2：房间不存在**

```json
{
  "code": 404,
  "message": "房间不存在",
  "data": null
}
```

**场景3：房间已解散，重复操作**

```json
{
  "code": 400,
  "message": "房间已解散，无需重复操作",
  "data": null
}
```

## 通用说明

1\. 所有接口的请求参数若为必填项，未填写或填写格式错误时，可根据实际项目规范返回对应错误码及提示信息（本文档未补充，可自行完善）。

2\. 响应状态码说明：200表示成功，400表示请求参数错误或业务逻辑错误，403表示权限不足，404表示资源不存在。

3\. token 有效期及失效处理逻辑，可根据实际项目的认证机制补充说明。

## 接口速查表

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| POST | /api/room/create | 创建房间 | 需要 |
| POST | /api/room/join | 加入房间 | 需要 |
| GET | /api/room/{roomCode} | 查询房间信息 | 需要 |
| POST | /api/room/leave | 退出房间 | 需要 |
| POST | /api/room/disband | 解散房间 | 需要 |
