# 接口文档

## 帖子相关接口

### 1. 发布帖子接口

#### 接口描述

用户发布新帖子，支持添加标题、内容、标签和图片。

#### 接口信息

- **接口名称**：发布帖子
- **请求方法**：POST
- **请求 URL**：`/post/add`

#### 请求参数

| 参数名      | 类型           | 是否必填 | 位置 | 描述                                   |
| ----------- | -------------- | -------- | ---- | -------------------------------------- |
| title       | String         | 是       | Body | 帖子标题，长度 2-100 个字符            |
| content     | String         | 是       | Body | 帖子内容，至少 5 个字符                |
| isAnonymous | Integer        | 是       | Body | 是否匿名发布，1 表示匿名，0 表示不匿名 |
| status      | Integer        | 是       | Body | 帖子状态，固定为 1                     |
| tags        | Array<Integer> | 是       | Body | 标签 ID 数组，至少选择一个标签         |
| images      | Array<String>  | 否       | Body | 图片 URL 数组，最多 9 张图片           |

#### 请求头

| 字段名       | 类型   | 说明         | 示例值                                  |
| ------------ | ------ | ------------ | --------------------------------------- |
| Content-Type | String | 请求内容类型 | application/json                        |
| token        | String | 用户认证令牌 | eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9... |

#### 响应状态码

| 状态码 | 描述                  | 说明                   |
| ------ | --------------------- | ---------------------- |
| 200    | OK                    | 请求成功               |
| 400    | Bad Request           | 请求参数错误           |
| 401    | Unauthorized          | 用户未登录或登录已过期 |
| 500    | Internal Server Error | 服务器内部错误         |

#### 响应数据格式

##### 成功响应

```json
{
  "code": 1,
  "message": "success",
  "data": "帖子发布成功"
}
```

##### 失败响应

```json
{
  "code": 0,
  "message": "发布失败，请重试",
  "data": null
}
```

#### 前端调用示例

```javascript
// PostPublishDialog.vue 中调用示例
import axios from 'axios';

async function handleSubmit() {
  try {
    // 验证表单
    await formRef.value.validate();

    // 设置提交状态
    isSubmitting.value = true;

    // 上传图片
    const uploadedImages = await uploadAllImages();

    // 构建请求数据（按接口要求格式）
    const requestData = {
      title: formData.title.trim(),
      content: formData.content.trim(),
      isAnonymous: formData.anonymous ? 1 : 0,
      status: 1, // 状态硬编码为1
      tags: formData.tags, // 已经是数字数组，不需要再转换
      images: uploadedImages, // 添加图片路径数组
    };

    // 发送请求到/post/add接口
    const response = await axios.post("/post/add", requestData, {
      headers: {
        token: ` ${userStore.token}`,
      },
    });

    // 处理成功响应
    if (response.data && response.data.code === 1) {
      console.log(response.data.data);
      ElMessage.success(response.data.data || "帖子发布成功");

      // 重置表单
      resetForm();

      // 关闭对话框
      visibleProxy.value = false;

      // 刷新页面
      window.location.reload();
    }
  } catch (error) {
    // 处理错误
    console.error("发布失败:", error);
    const anyError = error as any;
    // 处理401未授权错误
    if (anyError?.response?.status === 401) {
      ElMessage.error("请先登录");
      // 触发登录对话框显示
      userStore.setAuthModalVisible(true);
      // 关闭当前对话框
      visibleProxy.value = false;
    } else {
      ElMessage.error(anyError?.message || "发布失败，请重试");
    }
  } finally {
    // 重置提交状态
    isSubmitting.value = false;
  }
}

// 上传单张图片
const uploadImage = async (file: File) => {
  try {
    isImageUploading.value = true;
    imageUploadError.value = "";

    const formData = new FormData();
    formData.append("file", file);

    // 调用上传接口 - 使用正确的代理路径
    const response = await axios.post("/upload", formData, {
      headers: {
        "Content-Type": "multipart/form-data",
      },
    });

    // 从响应中提取URL并返回，确保正确处理/uploads路径
    if (response.data && response.data.url) {
      let url = response.data.url;

      // 处理URL格式，确保正确的/uploads路径格式
      if (url.startsWith("http://") || url.startsWith("https://")) {
        // 完整URL保持不变
        return url;
      } else if (url.startsWith("/uploads")) {
        // 已经是正确的/uploads开头路径
        return url;
      } else if (url.startsWith("uploads")) {
        // 添加前导斜杠
        return `/${url}`;
      } else {
        // 对于其他情况，确保以/uploads开头
        return url.includes("uploads")
          ? url.startsWith("/")
            ? url
            : `/${url}`
          : `/uploads/${url}`;
      }
    } else {
      throw new Error("上传成功但未返回图片URL");
    }
  } catch (error: any) {
    console.error("图片上传失败:", error);
    throw new Error(error?.response?.data?.message || "图片上传失败，请重试");
  } finally {
    isImageUploading.value = false;
  }
};
```

### 2. 文件上传接口

#### 接口描述

用于上传图片等文件，支持单文件上传。

#### 接口信息

- **接口名称**：文件上传
- **请求方法**：POST
- **请求 URL**：`/upload`

#### 请求参数

| 参数名 | 类型 | 是否必填 | 位置     | 描述                                         |
| ------ | ---- | -------- | -------- | -------------------------------------------- |
| file   | File | 是       | FormData | 要上传的文件，支持 JPG 和 PNG 格式，最大 2MB |

#### 请求头

| 字段名       | 类型   | 说明         | 示例值              |
| ------------ | ------ | ------------ | ------------------- |
| Content-Type | String | 请求内容类型 | multipart/form-data |

#### 响应状态码

| 状态码 | 描述                  | 说明           |
| ------ | --------------------- | -------------- |
| 200    | OK                    | 请求成功       |
| 400    | Bad Request           | 请求参数错误   |
| 500    | Internal Server Error | 服务器内部错误 |

#### 响应数据格式

##### 成功响应

```json
{
  "code": 1,
  "message": "success",
  "data": {
    "url": "/uploads/20240501/1234567890.jpg"
  }
}
```

##### 失败响应

```json
{
  "code": 0,
  "message": "上传失败",
  "data": null
}
```

## 日记记录相关接口文档

## 1. 获取用户一年内日记记录日期列表

### 接口描述

获取用户在指定年份内有写日记记录的所有日期，用于生成日记热力图日历。

### 接口信息

- **接口名称**：获取用户日记记录日期
- **请求方法**：GET
- **请求 URL**：`/api/diary/dates`

### 请求参数

| 参数名  | 类型    | 是否必填 | 位置  | 描述                                |
| ------- | ------- | -------- | ----- | ----------------------------------- |
| year    | Integer | 否       | Query | 指定年份，默认为当前年份            |
| user_id | Integer | 否       | Query | 用户 ID，不提供时默认为当前登录用户 |

### 请求头

| 字段名        | 类型   | 说明         | 示例值                                         |
| ------------- | ------ | ------------ | ---------------------------------------------- |
| Content-Type  | String | 请求内容类型 | application/json                               |
| Authorization | String | 用户认证令牌 | Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9... |

### 响应状态码

| 状态码 | 描述                  | 说明                 |
| ------ | --------------------- | -------------------- |
| 200    | OK                    | 请求成功             |
| 401    | Unauthorized          | 用户未认证           |
| 403    | Forbidden             | 用户无权限访问该资源 |
| 500    | Internal Server Error | 服务器内部错误       |

### 响应数据格式

#### 成功响应

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "year": 2024,
    "dates": [
      "2024-01-01",
      "2024-01-03",
      "2024-01-05",
      "2024-01-06",
      "2024-01-08"
      // ... 更多日期
    ],
    "total": 235
  }
}
```

#### 失败响应

```json
{
  "code": 401,
  "message": "用户未登录",
  "data": null
}
```

### 字段说明

| 字段名     | 类型    | 说明                                    |
| ---------- | ------- | --------------------------------------- |
| code       | Integer | 响应状态码                              |
| message    | String  | 响应消息                                |
| data       | Object  | 响应数据                                |
| data.year  | Integer | 请求的年份                              |
| data.dates | Array   | 有日记记录的日期列表，格式为 YYYY-MM-DD |
| data.total | Integer | 日记记录总天数                          |

### 请求示例

```http
GET /api/diary/dates?year=2024 HTTP/1.1
Host: example.com
Content-Type: application/json
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

## 2. 获取用户一年内日记列表

### 接口描述

获取用户在指定年份内的所有日记列表，支持分页。

### 接口信息

- **接口名称**：获取用户日记列表
- **请求方法**：GET
- **请求 URL**：`/api/diary/list`

### 请求参数

| 参数名     | 类型    | 是否必填 | 位置  | 描述                                     |
| ---------- | ------- | -------- | ----- | ---------------------------------------- |
| year       | Integer | 否       | Query | 指定年份，默认为当前年份                 |
| page       | Integer | 否       | Query | 页码，默认为 1                           |
| page_size  | Integer | 否       | Query | 每页条数，默认为 10                      |
| user_id    | Integer | 否       | Query | 用户 ID，不提供时默认为当前登录用户      |
| sort_by    | String  | 否       | Query | 排序字段，可选值：date, title            |
| sort_order | String  | 否       | Query | 排序方向，可选值：asc, desc，默认为 desc |

### 请求头

| 字段名        | 类型   | 说明         | 示例值                                         |
| ------------- | ------ | ------------ | ---------------------------------------------- |
| Content-Type  | String | 请求内容类型 | application/json                               |
| Authorization | String | 用户认证令牌 | Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9... |

### 响应状态码

| 状态码 | 描述                  | 说明                 |
| ------ | --------------------- | -------------------- |
| 200    | OK                    | 请求成功             |
| 401    | Unauthorized          | 用户未认证           |
| 403    | Forbidden             | 用户无权限访问该资源 |
| 500    | Internal Server Error | 服务器内部错误       |

### 响应数据格式

#### 成功响应

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "year": 2024,
    "total": 235,
    "page": 1,
    "page_size": 10,
    "total_pages": 24,
    "list": [
      {
        "id": 1,
        "title": "示例日记标题 A",
        "content": "这里是日记内容占位，未来接入后端数据。",
        "date": "2024-01-01",
        "created_at": "2024-01-01T10:30:00Z",
        "updated_at": "2024-01-01T10:30:00Z"
      },
      {
        "id": 2,
        "title": "示例日记标题 B",
        "content": "这里是日记内容占位，未来接入后端数据。",
        "date": "2024-01-03",
        "created_at": "2024-01-03T14:20:00Z",
        "updated_at": "2024-01-03T14:20:00Z"
      }
      // ... 更多日记项
    ]
  }
}
```

#### 失败响应

```json
{
  "code": 401,
  "message": "用户未登录",
  "data": null
}
```

### 字段说明

| 字段名                 | 类型    | 说明                        |
| ---------------------- | ------- | --------------------------- |
| code                   | Integer | 响应状态码                  |
| message                | String  | 响应消息                    |
| data                   | Object  | 响应数据                    |
| data.year              | Integer | 请求的年份                  |
| data.total             | Integer | 日记总条数                  |
| data.page              | Integer | 当前页码                    |
| data.page_size         | Integer | 每页条数                    |
| data.total_pages       | Integer | 总页数                      |
| data.list              | Array   | 日记列表                    |
| data.list[].id         | Integer | 日记 ID                     |
| data.list[].title      | String  | 日记标题                    |
| data.list[].content    | String  | 日记内容                    |
| data.list[].date       | String  | 日记日期，格式为 YYYY-MM-DD |
| data.list[].created_at | String  | 创建时间，格式为 ISO8601    |
| data.list[].updated_at | String  | 更新时间，格式为 ISO8601    |

### 请求示例

```http
GET /api/diary/list?year=2024&page=1&page_size=10&sort_by=date&sort_order=desc HTTP/1.1
Host: example.com
Content-Type: application/json
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

## 3. 前端调用示例

### 使用 Fetch API 调用

#### 获取日记记录日期列表

```javascript
// DiaryPage.vue 中调用示例
async function fetchHasDataDates(year) {
  try {
    const response = await fetch(`/api/diary/dates?year=${year}`, {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${localStorage.getItem("token")}`,
      },
    });

    const data = await response.json();
    if (data.code === 200) {
      return data.data.dates;
    } else {
      console.error("获取日记日期失败:", data.message);
      return [];
    }
  } catch (error) {
    console.error("获取日记日期出错:", error);
    return [];
  }
}

// 更新年份时调用
const updateYear = async (year) => {
  console.log(`切换到年份: ${year}`);
  hasDataDates.value = await fetchHasDataDates(year);
};
```

#### 获取日记列表

```javascript
// DiaryPage.vue 中调用示例
async function fetchPosts(year, page = 1, pageSize = 10) {
  try {
    const response = await fetch(
      `/api/diary/list?year=${year}&page=${page}&page_size=${pageSize}`,
      {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${localStorage.getItem("token")}`,
        },
      }
    );

    const data = await response.json();
    if (data.code === 200) {
      return data.data.list;
    } else {
      console.error("获取日记列表失败:", data.message);
      return [];
    }
  } catch (error) {
    console.error("获取日记列表出错:", error);
    return [];
  }
}

// 组件挂载时调用
onMounted(async () => {
  hasDataDates.value = await fetchHasDataDates(currentYear.value);
  posts.value = await fetchPosts(currentYear.value);
});
```

### 使用 Axios 调用（推荐）

#### 获取日记记录日期列表

```javascript
// DiaryPage.vue 中调用示例
import axios from "axios";

async function fetchHasDataDates(year) {
  try {
    const response = await axios.get("/api/diary/dates", {
      params: { year },
      headers: {
        Authorization: `Bearer ${localStorage.getItem("token")}`,
      },
    });

    if (response.data.code === 200) {
      return response.data.data.dates;
    } else {
      console.error("获取日记日期失败:", response.data.message);
      return [];
    }
  } catch (error) {
    console.error("获取日记日期出错:", error);
    return [];
  }
}
```

#### 获取日记列表

```javascript
// DiaryPage.vue 中调用示例
async function fetchPosts(year, page = 1, pageSize = 10) {
  try {
    const response = await axios.get("/api/diary/list", {
      params: {
        year,
        page,
        page_size: pageSize,
        sort_by: "date",
        sort_order: "desc",
      },
      headers: {
        Authorization: `Bearer ${localStorage.getItem("token")}`,
      },
    });

    if (response.data.code === 200) {
      return response.data.data.list;
    } else {
      console.error("获取日记列表失败:", response.data.message);
      return [];
    }
  } catch (error) {
    console.error("获取日记列表出错:", error);
    return [];
  }
}
```

## 4. 错误码说明

| 错误码 | 说明                   |
| ------ | ---------------------- |
| 200    | 操作成功               |
| 400    | 请求参数错误           |
| 401    | 用户未登录或登录已过期 |
| 403    | 用户无权限访问该资源   |
| 404    | 请求的资源不存在       |
| 500    | 服务器内部错误         |
| 501    | 该功能尚未实现         |
| 502    | 网关错误               |
| 503    | 服务暂时不可用         |
| 504    | 网关超时               |
