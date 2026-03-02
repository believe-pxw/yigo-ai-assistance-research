---
name: yigo-form-scaffold
description: 生成 YIGO Form XML 的骨架结构，包括 Form 根节点、DataSource、Body、OperationCollection、ScriptCollection 等顶层容器
---

# YIGO Form 脚手架生成

## 概述

本 Skill 负责生成 YIGO Form XML 的**顶层骨架结构**。Form 是 YIGO 系统中窗口配置的根对象，包含数据源、操作集合、脚本集合、窗体等核心子元素。

## XSD 参考文件

- 主文件：[Form.xsd](file:///d:/Workbench/idea/yigo-ai-assistance-research/resource/xsd/xsd/Form.xsd)
- 枚举定义：[FormDefine.xsd](file:///d:/Workbench/idea/yigo-ai-assistance-research/resource/xsd/xsd/element/simple/FormDefine.xsd)

## Form XML 顶层结构

```xml
<Form Key="表单标识" Caption="表单名称" FormType="Entity" InitState="Default" Version="6.1" Platform="PC">
    <!-- 1. 数据源配置 -->
    <DataSource RefObjectKey="关联数据对象Key">
        <DataObject>...</DataObject>  <!-- 可选：内嵌数据对象 -->
    </DataSource>
    
    <!-- 2. 操作定义集合 -->
    <OperationCollection>
        <Operation Key="..." Caption="..." />
    </OperationCollection>
    
    <!-- 3. 脚本集合 -->
    <ScriptCollection>...</ScriptCollection>
    
    <!-- 4. 窗体（UI 布局入口） -->
    <Body PopWidth="800" PopHeight="600" Resizable="true">
        <Block Key="block1" Caption="主区域">
            <!-- 面板/控件放在这里 -->
        </Block>
    </Body>
    
    <!-- 5. 导航条（移动端，可选） -->
    <NavigationBar />
    
    <!-- 6. 事件钩子（可选） -->
    <OnLoad>公式内容</OnLoad>
    <OnClose>公式内容</OnClose>
    <OnPostShow>公式内容</OnPostShow>
    
    <!-- 7. 宏公式集合（可选） -->
    <MacroCollection>
        <Macro Key="宏标识" Args="参数">公式内容</Macro>
    </MacroCollection>
    
    <!-- 8. 表单关系集合（复合字典用，可选） -->
    <FormRelationCollection>...</FormRelationCollection>
</Form>
```

## Form 根节点属性

| 属性 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `Key` | string(50) | ✅ | 表单唯一标识 |
| `Caption` | string | ❌ | 表单显示名称 |
| `CaptionEn` | string | ❌ | 表单英文名称 |
| `FormType` | 枚举 | ❌ | 表单类型，见下表 |
| `InitState` | 枚举 | ❌ | 打开初始状态 |
| `Version` | string | ❌ | 配置版本 |
| `Platform` | 枚举 | ❌ | 支持平台 |
| `FormulaCaption` | string | ❌ | 表达式名称 |
| `AbbrCaption` | string | ❌ | 缩写名称 |
| `ConfirmClose` | Boolean | ❌ | 关闭确认弹框 |
| `HasNavigationBar` | Boolean | ❌ | 是否有导航条 |
| `ViewKey` | string(50) | ❌ | 关联的 View 标识 |
| `Extend` | string(50) | ❌ | 继承的表单标识 |
| `SourceForm` | string(50) | ❌ | 源表单（扩展表单用） |
| `FullscreenType` | 枚举 | ❌ | 全屏类型（移动端） |
| `Authenticate` | Boolean | ❌ | 是否认证登录 |
| `DeliveryClass` | 枚举 | ❌ | 交付类 |
| `TransFormKey` | string | ❌ | 传输关联 FormKey |

## FormType 枚举值

| 值 | 说明 |
|----|------|
| `Normal` | 普通表单（基础类型） |
| `Entity` | 实体表单（单据） |
| `View` | 视图表单（叙时簿/浏览） |
| `Dict` | 字典表单 |
| `Detail` | 明细表单 |
| `Report` | 报表表单 |
| `ChainDict` | 链式字典表单 |
| `CompDict` | 复合字典表单 |
| `Template` | 模板表单 |
| `Extension` | 扩展表单 |

## InitState 枚举值

| 值 | 说明 |
|----|------|
| `Default` | 默认状态 |
| `New` | 新增状态 |
| `Edit` | 修改状态 |

## Platform 枚举值

| 值 | 说明 |
|----|------|
| `PC` | 电脑客户端 |
| `Android` | 安卓移动端 |
| `IOS` | iOS 移动端 |
| `H5` | H5 网页 |
| `Mobile` | 所有移动端 |
| `All` | 所有平台 |

## DataSource 节点

```xml
<DataSource RefObjectKey="数据对象Key">
    <!-- 可选：内嵌 DataObject 定义 -->
    <DataObject Key="..." Caption="..." PrimaryType="Entity">
        ...
    </DataObject>
</DataSource>
```

- `RefObjectKey`（可选）：引用已定义的数据对象标识

## Body 节点

```xml
<Body PopWidth="800" PopHeight="600" Resizable="true">
    <Block Key="block1" Caption="表头区域">
        <!-- 此处放置面板和控件 -->
    </Block>
    <ViewCollection>...</ViewCollection>
</Body>
```

| 属性 | 说明 |
|------|------|
| `PopWidth` | 弹出宽度 |
| `PopHeight` | 弹出高度 |
| `Resizable` | 是否可调整大小 |

## 校验规则

1. **有 ViewKey 必须是 Entity 类型**：`if(@ViewKey) then @FormType='Entity'`
2. **Key 长度限制**：不超过 50 字符
3. **子元素顺序**：`DataSource → OperationCollection → ScriptCollection → Body → NavigationBar → OnLoad → OnClose → OnPostShow → MacroCollection → FormRelationCollection`

## 使用示例

### 示例 1：标准实体表单（采购订单）

```xml
<Form Key="PurchaseOrder" Caption="采购订单" FormType="Entity" InitState="Default" Version="6.1" Platform="PC">
    <DataSource RefObjectKey="PurchaseOrder" />
    <OperationCollection>
        <!-- 由 yigo-operation-script skill 生成 -->
    </OperationCollection>
    <Body>
        <Block Key="mainBlock" Caption="主区域">
            <!-- 由 yigo-panel-layout skill 生成 -->
        </Block>
    </Body>
</Form>
```

### 示例 2：字典表单

```xml
<Form Key="CurrencyDict" Caption="币别字典" FormType="Dict" InitState="Default" Version="6.1">
    <DataSource RefObjectKey="CurrencyDict" />
    <Body>
        <Block Key="mainBlock">
            <!-- 面板布局 -->
        </Block>
    </Body>
</Form>
```

### 示例 3：View 表单（叙时簿）

```xml
<Form Key="PurchaseOrderView" Caption="采购订单叙时簿" FormType="View" ViewKey="PurchaseOrder" InitState="Default" Version="6.1">
    <DataSource RefObjectKey="PurchaseOrderView" />
    <Body>
        <Block Key="mainBlock">
            <!-- 叙时簿通常包含一个 Grid 表格 -->
        </Block>
    </Body>
</Form>
```

## 与其他 Skill 的配合

- **Body > Block** 内的面板/控件  →  使用 `yigo-panel-layout` 和 `yigo-control-generator`
- **DataSource > DataObject** → 使用 `yigo-dataobject-generator`
- **OperationCollection** → 使用 `yigo-operation-script`
- **OnLoad/OnClose 等事件内容** → 使用 `yigo-expression-helper`
