# YIGO ERP 完整技术参考手册

> 本文档将 `yigo-erp-architecture.md`、`.agent/module-naming-conventions.md`、`peixw.bnf` 以及所有 Skill 文档合并为一份完整参考。文档内各章节之间的交叉引用使用 `→ 参考 [章节名](#锚点)` 的形式。

---

<!-- =========================================================== -->
# 第一部分：平台架构与基础概念
<!-- =========================================================== -->

<a id="architecture"></a>
## 1. YIGO 平台与 YIGO ERP 产品

1. 单页面 Web 应用
2. 页面都是通过 Form 表单渲染形成
3. Form 表单都是通过 XML 文件配置，里面包含了页面的布局，数据对象（DataObject，里面包含了表的定义，里面的表字段称之为 Column），界面操作（Operation），控件（Control, ERP 中称之为 Field），事件（ValueChanged, Button 等），宏定义（Macro），在事件与宏中支持表达式 → 语法参考 [BNF 语法定义](#bnf-grammar)
4. 前后端交互是通过 `RichDocumentDefaultCmd.java` 这个服务，其中，`dealArguments` 代表将 JSON 数据转化为 `DocumentRecordDirty` 对象，`getDirtyJSON` 代表将 `DocumentRecordDirty` 对象转化为 JSON 数据返回给前端
5. `RichDocument.java` 是后端处理页面数据的类，也代表一个 Form 表单，是 `DocumentRecordDirty` 的父类，里面提供了 `setValue`、`getValue` 等方法
6. 二次开发：表示基于上述通用模式下，进行二次开发，二次开发的类需要继承 `EntityContextAction` → 参考 [Java 二次开发](#java-customization)。二次开发使用 Java 进行编写，其中，内部变量 `_context` 代表 `RichDocumentContext`，代表当前上下文，里面记录了 `RichDocument` 对象，也就是当前的表单数据。二次开发在 XML 中通过 `类名.方法名` 的方式调用 Java 方法
7. 二次开发中，YIGO ERP 通过将 Form 的 XML 文件转换成 `BillEntity`，里面封装了若干方法，可在 Java 中快速对字段或表进行增删改操作，实际上底层都是通过 `RichDocument` 对象来执行，同时，还提供了 `formkey_loader.java` 文件，里面根据 FieldKey 封装了若干方法，这些方法都是查询条件，最后通过 `load()` 方法可以从数据库中查询出数据并转换为 `RichDocument` 对象。还有 `TableEntity`，这种是表单内的某张表的封装，与 `BillEntity` 的差异就是内部是一个 `DataTable`，使用 `ColumnKey` 进行访问
8. `RichDocumentContext` 中承载着数据库连接，如果需要 SQL 可通过 `SqlString` 拼接后再调用 `RichDocumentContext#getResultSet(SqlString)` 方法执行，返回结果集
9. `RichDocumentContext` 中有一个 `Paras` 键值对对象，可以在上下文中存储任何自定义参数
10. `entry.xml` 中定义了菜单入口，打开菜单后会根据 FormKey 打开对应的 Form 表单
11. `Commondef.xml` 中定义了顶层宏公式，在 Form 中可以调用，注意：Form 中同名的宏公式会覆盖 `Commondef.xml` 中的宏公式
12. 由于是 ERP 系统，现在 XML 与 Java 二开代码都已经有百万行代码之多
13. 预定义数据：每一个 Form，特别是后台管理类的 Form，都会有一个预定义数据 → 参考 [预定义数据生成](#predefined-data)

<a id="workflow"></a>
## 2. YIGO ERP 工作流

1. 我们的 ERP 是对标 SAP ERP 的，所以绝大部分业务逻辑类似
2. 首先，我们会先进行需求分析，确定表单需要哪些控件 Field，哪些 Column（是否需要持久化），哪些 Operation（操作）
3. 接下来会将界面画出来，再填充业务逻辑，比如字段的值变化事件、默认值表达式、CheckRule、Macro 等
4. 会将画好的 Form 加入进菜单（如果需要）
5. 最后，会进行测试，确保业务逻辑正确，界面显示正确，操作流程正确

<a id="role-definition"></a>
## 3. 角色定义

1. 你是一个 SAP ERP 技术专家与资深顾问，现在由你基于 ERP 知识底座来实现任务。
2. SAP 与 YIGO ERP 的业务体系类似，在思考时可先参考 YIGO ERP 对应的 SAP 实现（如果存在的话）

---

<!-- =========================================================== -->
# 第二部分：模块命名规范
<!-- =========================================================== -->

<a id="module-naming"></a>
## 4. YIGO ERP 模块命名规范

> 本章定义了各业务模块的 FormKey、Table Key 命名前缀规则。
> 所有生成 DataObject、Form、预定义数据时**必须遵循**本规范。

### 4.1 命名规则总览

**通用规则**：
- **FormKey / DataObject Key** = `{模块前缀}_{业务名称}`，如 `MM_PurchaseOrder`
- **Table Key** = `E{模块前缀}_{业务名称}`，如 `EMM_PurchaseOrderHead`
- Table Key 以 `E` 开头（Entity 的缩写），后接模块前缀

### 4.2 模块命名映射表

| Project Key | Caption | 模块前缀 | FormKey 示例 | Table 前缀 | Table 示例 |
|---|---|---|---|---|---|
| `basisconfig` | BASIS | GS | `GS_UserParam` | `EGS_` | `EGS_UserParamHead` |
| `BK_Basic` | 基本资料 | BK | `FavoriteVariant` | `BK_` | `BK_FavoriteVariant` |
| `mmconfig` | MM | MM | `MM_PurchaseOrder` | `EMM_` | `EMM_PurchaseOrderHead` |
| `sdconfig` | SD | SD | `SD_SaleOrder` | `ESD_` | `ESD_SaleOrderHead` |
| `pmconfig` | PM | PM | `PM_MaintenanceOrder` | `EPM_` | `EPM_MaintenanceOrderHead` |
| `ppconfig` | PP | PP | `PP_ProductionOrder` | `EPP_` | `EPP_ProductionOrder` |
| `ficonfig` | FI | FI | `FI_Voucher` | `EFI_` | `EFI_VoucherHead` |
| `coconfig` | CO | CO | `CO_ProductionOrder` | `ECO_` | `ECO_ProductionOrder` |
| `hrconfig` | HR | HR | `HR_Organization` | `EHR_` | `EHR_Object` |
| `qmconfig` | QM | QM | `QM_InspectionLot` | `EQM_` | `EQM_InspectionLot` |
| `psconfig` | PS | PS | `PS_Project` | `EPS_` | `EPS_Project` |
| `wmsconfig` | WMS | WM | `WM_ReceiptOrder` | `EWM_` | `EWM_ReceiptOrderHead` |
| `tmconfig` | TM | TM | `TM_Shipment` | `ETM_` | `ETM_ShipmentHead` |
| `fmconfig` | 基金管理 | FM | `FM_FundVoucher` | `EFM_` | `EFM_FundVoucherHead` |
| `copaconfig` | COPA | COPA | `COPA_ProfitSegment` | `ECOPA_` | `ECOPA_ProfitSegment` |
| `tcmconfig` | 资金管理 | TCM | `TCM_CollectionOrder` | `ETCM_` | `ETCM_CollectionOrderHead` |
| `authorityConfig` | 权限 | AU | `AuthorityFieldValue` | `EAU_` | `EAU_StructureParameterFileHead` |

#### FI 模块子模块

| 子模块 | 业务域 | FormKey 前缀 | Table 前缀 | 示例 |
|---|---|---|---|---|
| AM | 资产管理 | `AM_` | `EAM_` | `EAM_AssetCard` |
| BM | 票据管理 | `BM_` | `EBM_` | `EBM_CommercialDraftHead` |
| ECS | 费用管理 | `ECS_` | `EECS_` | `EECS_ExpenseRequisitionHead` |

### 4.3 跨模块公共对象

以下对象不属于特定业务模块，使用 `EGS_` 前缀：

| 对象 | Table 前缀 | 说明 |
|---|---|---|
| `TCode` | `EGS_TCode` | 事务码 |
| `Message` | `EGS_Message` | 系统消息 |
| `MessageClass` | `EGS_MessageClass` | 消息类 |
| `EntryTCodeRelation` | `EAU_EntryTCodeRelation` | 菜单事务码关系 |
| `TCodeAuthorityObjectFieldDefaultValue` | `EAU_TCodeAuthObjectDefRel` | 事务码权限对象默认值 |
| `TCodeAuthorityObjectFieldValue` | `EAU_TCodeAuthorityObjectRelDtl` | 事务码权限对象字段值 |
| `AuthorityObject` | `EAU_AuthorityObject` | 权限对象 |

### 4.4 使用指南

#### 确定新对象前缀

1. 根据业务需求确定目标模块（如采购 → MM）
2. 查表获取 FormKey 前缀（`MM_`）和 Table 前缀（`EMM_`）
3. FormKey = `{前缀}{业务名}` → `MM_PurchaseOrder`
4. 主表 Key = `{Table前缀}{业务名}Head` → `EMM_PurchaseOrderHead`（单据类）
5. 主表 Key = `{Table前缀}{业务名}` → `EMM_MoveType`（字典类，不加 Head）
6. 明细表 Key = `{Table前缀}{业务名}Dtl` → `EMM_PurchaseOrderDtl`

#### 主表命名后缀

| 类型 | 主表是否加 Head | 示例 |
|---|---|---|
| 单据表单 | 加 `Head` | `EMM_PurchaseOrderHead` |
| 字典表单 | 不加 | `EPM_OrderType` |
| 迁移表 | 不加 | `EMM_MaterialStorage` |

---

<!-- =========================================================== -->
# 第三部分：BNF 表达式语法定义
<!-- =========================================================== -->

<a id="bnf-grammar"></a>
## 5. YIGO 表达式 BNF 语法

> 完整 BNF 定义，用于 Form XML 中各种公式场景。→ 表达式书写指南 [表达式书写](#expression-writer)

```bnf
// MyLanguage.bnf - Context-Aware Version with Decimal Support and Comments

{
  tokens = [
    IF_KEYWORD = "if"
    ELSE_KEYWORD = "else"
    WHILE_KEYWORD = "while"
    RETURN_KEYWORD = "return"
    VAR_KEYWORD = "var"
    PARENT_KEYWORD = "regexp:[pP]arent"
    CONTAINER_KEYWORD = "regexp:[cC]ontainer"
    TRUE_KEYWORD = "true"
    FALSE_KEYWORD = "false"
    IIF_KEYWORD = "IIF"

    // 特殊函数标识符
    CONFIRM_MSG = "ConfirmMsg"

    MACRO_IDENTIFIER = "regexp:Macro_[a-zA-Z_][a-zA-Z0-9_]*"
    JAVA_PATH_IDENTIFIER = "regexp:com*(\.[a-zA-Z_][a-zA-Z0-9_]*)+"
    IDENTIFIER = "regexp:[a-zA-Z_][a-zA-Z0-9_]*"

    SINGLE_QUOTED_STRING = "regexp:'[^']*'"
    DOUBLE_QUOTED_STRING = "regexp:\"[^\"]*\""

    // 扩展数字类型以支持小数
    DECIMAL_NUMBER = "regexp:[0-9]+\.[0-9]+"  // 小数：如 0.00, 3.14, 123.456
    INTEGER_NUMBER = "regexp:[0-9]+"          // 整数：如 0, 123, 456
    NUMBER = "regexp:[0-9]+(\.[0-9]+)?"       // 通用数字匹配（整数或小数）

    // 注释 tokens
    LINE_COMMENT = "regexp://.*"                           // 单行注释：// 开头到行尾
    BLOCK_COMMENT = "regexp:/\*([^*]|\*+[^*/])*\*+/"      // 多行注释：/* ... */

    AMP_ENTITY = "&amp;"
    LT_ENTITY = "&lt;"
    GT_ENTITY = "&gt;"
    AND_OP_ENTITY = "&amp;&amp;"

    PLUS = "+"
    MINUS = "-"
    MUL = "*"
    DIV = "/"
    AMPERSAND = "&"
    AND_OP = "&&"
    OR_OP = "||"
    DOT = "."
    NOT_OP = "!"
    EQ = "="

    LESS_EQUAL = "<="
    GREATER_EQUAL = ">="
    EQUAL_EQUAL = "=="
    NOT_EQUAL = "!="
    LESS = "<"
    GREATER = ">"
    NOT_EQUAL_ALT = "<>"

    LPAREN = "("
    RPAREN = ")"
    SEMICOLON = ";"
    COMMA = ","
    LBRACE = "{"
    RBRACE = "}"
    COLON = ":"
  ]
}

// ---------------------------------------------------------------------
// 语法规则 (Grammar Rules)
// ---------------------------------------------------------------------

root ::= (comment | top_level_statement)*

// 注释规则
comment ::= LINE_COMMENT | BLOCK_COMMENT

private top_level_statement ::=
  (variable_declaration SEMICOLON)
  | (variable_assignment SEMICOLON?)
  | ((expression_sequence | if_statement | while_statement |return_statement) SEMICOLON?)

expression_sequence ::= expression_statement (SEMICOLON expression_statement)*

statement ::=
  comment
  | (variable_declaration SEMICOLON)
  | (variable_assignment SEMICOLON?)
  | ((expression_sequence | if_statement | while_statement | return_statement) SEMICOLON?)

variable_declaration ::= VAR_KEYWORD IDENTIFIER (EQ expression)?
variable_assignment ::= IDENTIFIER EQ expression
expression_statement ::= expression

if_statement ::= IF_KEYWORD LPAREN expression RPAREN statement_block (ELSE_KEYWORD statement_block)?

while_statement ::= WHILE_KEYWORD LPAREN expression RPAREN statement_block
return_statement ::= RETURN_KEYWORD expression

statement_block ::= statement | block_statement

block_statement ::= LBRACE (comment | statement)* RBRACE COLON*

expression ::= logical_or_expression
private logical_or_expression ::= logical_and_expression (OR_OP logical_and_expression)*
private logical_and_expression ::= comparison_expression ((AND_OP|AND_OP_ENTITY) comparison_expression)*

private comparison_expression ::= additive_expression ((LESS_EQUAL | GREATER_EQUAL | EQUAL_EQUAL | NOT_EQUAL | LESS | GREATER | NOT_EQUAL_ALT | LT_ENTITY | GT_ENTITY) additive_expression)*

private additive_expression ::= multiplicative_expression ((PLUS | MINUS | AMPERSAND | AMP_ENTITY) multiplicative_expression)*

private multiplicative_expression ::= unary_expression ((MUL | DIV) unary_expression)*
private unary_expression ::= (MINUS | NOT_OP) unary_expression | primary_expression

primary_expression ::=
  constant
  | function_call
  | variable_reference
  | LPAREN expression RPAREN
  | boolean_constant

variable_reference ::= path

// 扩展常量规则以支持不同类型的数字
constant ::=
  SINGLE_QUOTED_STRING
  | DOUBLE_QUOTED_STRING
  | numeric_constant

// 新增数字常量规则，支持小数和整数
numeric_constant ::=
  DECIMAL_NUMBER      // 优先匹配小数
  | INTEGER_NUMBER    // 然后匹配整数

boolean_constant ::= TRUE_KEYWORD | FALSE_KEYWORD

// 关键改进：区分不同类型的函数调用
function_call ::=
  ((PARENT_KEYWORD | CONTAINER_KEYWORD) DOT)?
  (
    confirm_msg_call |          // 特殊处理 ConfirmMsg
    regular_function_call       // 普通函数调用
  )

// ConfirmMsg 特殊处理：支持新的参数格式
confirm_msg_call ::= CONFIRM_MSG LPAREN confirm_msg_args RPAREN
confirm_msg_args ::=
  expression COMMA expression                                              // 必需：args[0]消息代码, args[1]消息文本
  (COMMA message_params_expression                                        // 可选：args[2]消息参数
    (COMMA expression                                                     // 可选：args[3]样式(OK,YES_NO,YES_NO_CANCEL)
      (COMMA callback_object)?                                            // 可选：args[4]回调函数对象
    )?
  )?

// 消息参数表达式：支持多种格式
message_params_expression ::=
  parameter_array |           // {{参数1},{参数2},{参数3}} 的形式
  double_brace_expression |   // {{表达式}} 的形式
  object_literal |            // {} 空对象形式
  expression                  // 其他表达式形式

// 双大括号表达式：{{表达式}}
double_brace_expression ::= LBRACE LBRACE expression RBRACE RBRACE

// 参数数组：{{param1},{param2},{param3}}
parameter_array ::= LBRACE LBRACE expression RBRACE (COMMA LBRACE expression RBRACE)* RBRACE

// 回调函数对象：专门用于ConfirmMsg的回调
callback_object ::= LBRACE callback_property (COMMA callback_property)* RBRACE
callback_property ::= callback_key COLON code_block_literal
callback_key ::=
  SINGLE_QUOTED_STRING | DOUBLE_QUOTED_STRING |  // 字符串形式的键
  IDENTIFIER                                     // 或标识符形式

// 普通函数调用
regular_function_call ::=
  (macro_call_expression | path | java_method_call | iif_function_call)
  LPAREN argument_list? RPAREN

// 对象字面量：专门用于函数参数中的字面量对象
object_literal ::= LBRACE object_literal_content RBRACE
object_literal_content ::= (object_property (COMMA object_property)*)?
object_property ::= IDENTIFIER COLON (object_literal | expression | code_block_literal)

// 代码块字面量：在对象属性中表示要执行的代码
code_block_literal ::= LBRACE (comment | statement)* RBRACE

macro_call_expression ::= MACRO_IDENTIFIER
java_method_call ::= JAVA_PATH_IDENTIFIER
iif_function_call ::= IIF_KEYWORD
argument_list ::= expression (COMMA expression)*
private path ::= ((PARENT_KEYWORD | CONTAINER_KEYWORD) DOT)? IDENTIFIER
```

---

<!-- =========================================================== -->
# 第四部分：数据层（Domain → DataElement → DataObject）
<!-- =========================================================== -->

<a id="domain-generator"></a>
# YIGO Domain 数据域生成

## 概述

Domain（数据域）是 YIGO 系统的 **字段类型定义层**，介于 DataElement（字段元数据）和 UI 控件之间。Domain 定义了：

- 关联的 UI 控件类型（RefControlType）
- 底层数据类型（DataType）
- 类型特有参数（长度、精度、字典引用、枚举值等）

Domain 文件按 **RefControlType 分文件组织**，每种控件类型对应一个 `DomainDef_{Type}.xml`。生成新条目时需追加到对应文件中。

## XSD 参考

- 根定义：DomainDef.xsd
- 专有属性：DomainDefDefine.xsd

## XML 骨架

```xml
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<DomainDef>
    <DomainCollection>
        <Domain .../>
        <!-- 或含 Item 子元素（ComboBox） -->
        <Domain ...>
            <Item Key="..." Caption="..." Value="..."/>
        </Domain>
    </DomainCollection>
</DomainDef>
```

## Domain 通用属性

| 属性 | 必须 | 说明 |
|------|------|------|
| `Key` | ✅ | 唯一标识，PascalCase |
| `Caption` | ✅ | 中文显示名 |
| `RefControlType` | ✅ | 关联 UI 控件类型（见下方分类表） |
| `DataType` | ✅ | 底层数据类型（见下方取值） |

### DataType 取值

| DataType | 说明 | 典型搭配 |
|----------|------|----------|
| `Varchar` | 变长字符串 | TextEditor, ComboBox |
| `Integer` | 整数 | NumberEditor, ComboBox, CheckBox |
| `Long` | 长整数 | NumberEditor, Dict, DynamicDict, UTCDatePicker |
| `Numeric` | 定点数（需 Precision + Scale） | NumberEditor |
| `Date` | 日期 | DatePicker, MonthPicker |
| `DateTime` | 日期时间 | DatePicker, TimePicker |

## 按 RefControlType 分类的属性规则

### TextEditor — 文本编辑器

```xml
<Domain Key="Varchar_255" Caption="文本_255" RefControlType="TextEditor" DataType="Varchar" Length="255"/>
<Domain Key="Code_30" Caption="代码_30" RefControlType="TextEditor" DataType="Varchar" Length="30"/>
```

| 特有属性 | 必须 | 说明 |
|----------|------|------|
| `Length` | ✅ | 最大字符长度 |

→ 追加到 `DomainDef_TextEditor.xml`

---

### NumberEditor — 数字编辑器

**Numeric 类型（定点数）**：
```xml
<Domain Key="Money_16_2" Caption="金额_精度16_小数位2" RefControlType="NumberEditor" DataType="Numeric" Precision="16" Scale="2"/>
<Domain Key="Quantity_16_3" Caption="数量_精度16_小数位3" RefControlType="NumberEditor" DataType="Numeric" Precision="16" Scale="3"/>
```

**Integer / Long 类型**：
```xml
<Domain Key="Numeric_Integer" Caption="数值_整型" RefControlType="NumberEditor" DataType="Integer"/>
<Domain Key="Numeric_Long" Caption="数值_长整型" RefControlType="NumberEditor" DataType="Long"/>
```

| 特有属性 | 条件 | 说明 |
|----------|------|------|
| `Precision` | DataType=Numeric 时必须 | 总精度位数 |
| `Scale` | DataType=Numeric 时必须 | 小数位数 |

→ 追加到 `DomainDef_NumberEditor.xml`

---

### DatePicker — 日期选择器

```xml
<Domain Key="Date" Caption="日期" RefControlType="DatePicker" DataType="Date"/>
<Domain Key="DateTime" Caption="时间" RefControlType="DatePicker" DataType="DateTime"/>
```

无特有属性。→ 追加到 `DomainDef_DatePicker.xml`

---

### CheckBox — 复选框

```xml
<Domain Key="CheckBox" Caption="复选框" RefControlType="CheckBox" DataType="Integer"/>
```

无特有属性，通常 DataType 为 `Integer`。→ 追加到 `DomainDef_CheckBox.xml`

---

### Dict — 字典选择

```xml
<Domain Key="IM_InvestProgram" Caption="投资程序" RefControlType="Dict" DataType="Long" ItemKey="IM_InvestProgram"/>
```

| 特有属性 | 必须 | 说明 |
|----------|------|------|
| `ItemKey` | ✅ | 引用的字典 Key |
| `AllowMultiSelection` | ❌ | 是否多选（`true`/`false`） |

→ 追加到 `DomainDef_Dictionary.xml`（多选字典追加到 `DomainDef_DynamicDict.xml`）

---

### DynamicDict — 动态字典

```xml
<Domain Key="DY_Factory" Caption="工厂（动态）" RefControlType="DynamicDict" DataType="Long"/>
```

属性同 Dict。→ 追加到 `DomainDef_DynamicDict.xml` 提醒用户DynamicDict需要自行指定ItemKeyCollection

---

### ComboBox — 下拉框 ⭐

ComboBox 最复杂，有 **三种 SourceType 模式**：

#### 模式 1：ParaGroup（参数组引用）

```xml
<Domain Key="BudgetType" Caption="预算类型" RefControlType="ComboBox" DataType="Integer" SourceType="ParaGroup" GroupKey="BudgetType"/>
```

| 属性 | 说明 |
|------|------|
| `SourceType` | 固定为 `ParaGroup` |
| `GroupKey` | 引用的参数组 Key |
| `Length` | Varchar 时可选 |

无子元素。

#### 模式 2：Items（内联枚举）

```xml
<Domain Key="AdjustStatus" Caption="状态" RefControlType="ComboBox" DataType="Integer" SourceType="Items">
    <Item Key="0" Caption="未开始" Value="0"/>
    <Item Key="1" Caption="进行中" Value="1"/>
    <Item Key="2" Caption="已完成" Value="2"/>
</Domain>
```

| 属性 | 说明 |
|------|------|
| `SourceType` | 固定为 `Items` |
| `Length` | Varchar 时可选 |
| 子元素 `Item` | `Key`(必须) + `Caption`(必须) + `Value`(必须) |

#### 模式 3：Formula（公式计算）

```xml
<Domain Key="AvsCondition" Caption="条件" RefControlType="ComboBox" DataType="Integer" SourceType="Formula"/>
```

| 属性 | 说明 |
|------|------|
| `SourceType` | 固定为 `Formula` |
| `Length` | Varchar 时可选 |

无子元素、无 GroupKey。

#### 模式 4：Status（状态）

```xml
<Domain Key="BusinessTripStatus" Caption="状态" RefControlType="ComboBox" DataType="Integer" SourceType="Status"/>
```

无子元素。

→ 所有 ComboBox 追加到 `DomainDef_ComboBox.xml`

---

### 其他控件类型

| RefControlType | DataType | 目标文件 | 说明 |
|----------------|----------|----------|------|
| `UTCDatePicker` | `Long` | `DomainDef_UTCDatePicker.xml` | UTC 日期 |
| `MonthPicker` | `Date` | `DomainDef_MonthPicker.xml` | 月份 |
| `TimePicker` | `DateTime` | `DomainDef_TimePicker.xml` | 时间 |
| `CheckListBox` | `Varchar` | `DomainDef_CheckListBox.xml` | 复选列表 |
| `TextArea` | `Varchar` | `DomainDef_TextArea.xml` | 多行文本 |
| `PasswordEditor` | `Varchar` | `DomainDef_PasswordEditor.xml` | 密码 |
| `RichEditor` | `Varchar` | `DomainDef_RichEditor.xml` | 富文本 |
| `Image` | `Varchar` | `DomainDef_Image.xml` | 图片 |
| `Button` | — | `DomainDef_Button.xml` | 按钮 |
| `TextButton` | — | `DomainDef_TextButton.xml` | 文本按钮 |
| `Separator` | — | `DomainDef_Separator.xml` | 分隔线 |
| `WebBrowser` | `Varchar` | `DomainDef_WebBrowser.xml` | 浏览器 |

## 生成规则

### 1. 选择 RefControlType

根据用户描述的字段用途自动推断：

| 字段描述 | RefControlType | DataType |
|----------|----------------|----------|
| 文本、代码、名称 | TextEditor | Varchar |
| 数字、金额、数量、价格 | NumberEditor | Numeric/Integer/Long |
| 日期 | DatePicker | Date/DateTime |
| 是否、勾选 | CheckBox | Integer |
| 字典引用、选择实体 | Dict | Long |
| 下拉选择、状态、类型枚举 | ComboBox | Varchar/Integer |
| 密码 | PasswordEditor | Varchar |
| 大段文本、描述、备注 | TextArea | Varchar |

### 2. Key 命名

- PascalCase，含模块前缀时用 `_` 分隔（如 `AM_InvestmentReason`）
- 描述精度的 Key 格式：`{类型}_{精度}_{小数位}`（如 `Money_16_2`、`Numeric_5_2`）

### 3. 追加位置

新条目追加到目标文件的 `</DomainCollection>` **前一行**（保持缩进一致）。

## 生成示例

用户请求：生成"订单状态"下拉框 Domain，枚举值为 0=未提交、1=已提交、2=已审批

```xml
<Domain Key="OrderStatus" Caption="订单状态" RefControlType="ComboBox" DataType="Integer" SourceType="Items">
    <Item Key="0" Caption="未提交" Value="0"/>
    <Item Key="1" Caption="已提交" Value="1"/>
    <Item Key="2" Caption="已审批" Value="2"/>
</Domain>
```

→ 追加到 `DomainDef_ComboBox.xml` 的 `<DomainCollection>` 内。


---

<a id="dataelement-generator"></a>
# YIGO DataElement 生成

## 概述

DataElement（数据元素）是 YIGO 系统的 **字段级元数据定义**。每个 DataElement 关联一个 Domain（数据域），并携带显示标题、多语言标签、变更日志标记等信息。

DataElement 文件按 **RefControlType 分文件组织**，每种控件类型对应一个 `DataElementDef_{Type}.xml`。生成新条目时需追加到对应文件中。

## XSD 参考

- 根定义：DataElementDef.xsd
- 专有属性：DataElementDefDefine.xsd

## XML 骨架

```xml
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<DataElementDef>
    <DataElementCollection>
        <DataElement .../>
        <!-- 或 -->
        <DataElement ...>
            <FieldLabelCollection>...</FieldLabelCollection>
        </DataElement>
    </DataElementCollection>
</DataElementDef>
```

## DataElement 属性

| 属性 | 必须 | 说明 |
|------|------|------|
| `Key` | ✅ | 唯一标识，PascalCase（如 `InvestReasonID`） |
| `Caption` | ✅ | 中文显示名（如 `投资原因`） |
| `DomainKey` | ✅ | 关联的 Domain Key（如 `Varchar_255`、`Money_16_2`、`Date`） |
| `DataDiffLog` | ❌ | 变更日志跟踪，`true`/`false`（绝大多数为 `true`） |
| `ParamID` | ❌ | 运行时参数标识（罕见） |

## FieldLabelCollection

可选子元素，提供 4 种长度的多语言标签。

### 完整格式（推荐）

```xml
<DataElement Key="InvestReasonID" Caption="投资原因" DomainKey="AM_InvestmentReason" DataDiffLog="true">
    <FieldLabelCollection>
        <FieldLabel Key="Short" Length="10" Text="投资原因" TextEn="Investment Reason"/>
        <FieldLabel Key="Medium" Length="15" Text="投资原因" TextEn="Investment Reason"/>
        <FieldLabel Key="Long" Length="20" Text="投资原因" TextEn="Investment Reason"/>
        <FieldLabel Key="Header" Text="投资原因" TextEn="Investment Reason"/>
    </FieldLabelCollection>
</DataElement>
```

### 简写格式（无 Length）

当 4 种标签文本完全相同且不需要指定显示长度时：

```xml
<DataElement Key="ResultKey" Caption="信息" DomainKey="Varchar_255" DataDiffLog="true">
    <FieldLabelCollection>
        <FieldLabel Key="Short" Text="信息" TextEn="Info"/>
        <FieldLabel Key="Medium" Text="信息" TextEn="Info"/>
        <FieldLabel Key="Long" Text="信息" TextEn="Info"/>
        <FieldLabel Key="Header" Text="信息" TextEn="Info"/>
    </FieldLabelCollection>
</DataElement>
```

### 最简格式（无 FieldLabel）

仅当不需要多语言标签时使用自闭合标签：

```xml
<DataElement Key="AccessControl" Caption="是否访问控制" DomainKey="AccessControl"/>
```

### FieldLabel 属性

| 属性 | 必须 | 说明 |
|------|------|------|
| `Key` | ✅ | 标签类型：`Short` / `Medium` / `Long` / `Header` |
| `Text` | ✅ | 中文标签文本 |
| `TextEn` | ✅ | 英文标签文本 |
| `Length` | ❌ | 显示长度（数字、字符数） |

## 生成规则

### 1. 确定目标文件

根据字段关联的 DomainKey **对应的 RefControlType** 确定写入哪个文件：

| DomainKey 模式 | RefControlType | 目标文件 |
|----------------|----------------|----------|
| `Varchar_*`、`Name_*`、`Code_*` | TextEditor | `DataElementDef_TextEditor.xml` |
| `Money_16_2` | NumberEditor | `DataElementDef_NumberEditor_Money_16_2.xml` |
| `Quantity_16_3` | NumberEditor | `DataElementDef_NumberEditor_Quantity_16_3.xml` |
| `Numeric_*`、`Price_*` 等其他数值 | NumberEditor | `DataElementDef_NumberEditor.xml` |
| `Date`、`DateTime` | DatePicker | `DataElementDef_DatePicker.xml` |
| `CheckBox` | CheckBox | `DataElementDef_CheckBox.xml` |
| 字典类 DomainKey（DataType=Long, ItemKey=xxx） | Dict | `DataElementDef_Dictionary.xml` |
| ComboBox 类 DomainKey | ComboBox | `DataElementDef_ComboBox.xml` |
| 系统字段类 | SystemField | `DataElementDef_SystemField.xml` |

> 当不确定 DomainKey 对应的 RefControlType 时，查看 `resource/Domain/` 下对应的 DomainDef 文件确认。

### 2. Key 命名

- 使用 **PascalCase**（如 `OrderStatus`、`CompanyCode`）
- 业务术语保持英文（如 `AM_` 前缀表示资产管理模块）
- Key 可以与 DomainKey 相同（如 `AccountType` → DomainKey `AccountType`）
- Key 也可以与 DomainKey 不同（如 `ReconAccountType` → DomainKey `AccountType`）

### 3. DataDiffLog 默认值

- 取决于字段是否要记录差异日志

### 4. FieldLabel 生成

- **默认行为**：生成完整的 4 个 FieldLabel（Short/Medium/Long/Header）
- **Text**：与 Caption 相同或略作简化
- **TextEn**：Caption 的英文翻译
- **Length**：可选，按需设置（Short 常用 10，Medium 常用 15，Long 常用 20）

### 5. 追加位置

新条目追加到目标文件的 `</DataElementCollection>` **前一行**（保持与文件中已有条目同级缩进）。

## 生成示例

用户请求：为"公司代码"字段生成 DataElement，类型为文本（Varchar 30 位）

```xml
<DataElement Key="CompanyCode" Caption="公司代码" DomainKey="Code_30" DataDiffLog="true">
    <FieldLabelCollection>
        <FieldLabel Key="Short" Length="10" Text="公司代码" TextEn="Company Code"/>
        <FieldLabel Key="Medium" Length="15" Text="公司代码" TextEn="Company Code"/>
        <FieldLabel Key="Long" Length="20" Text="公司代码" TextEn="Company Code"/>
        <FieldLabel Key="Header" Text="公司代码" TextEn="Company Code"/>
    </FieldLabelCollection>
</DataElement>
```

→ 追加到 `DataElementDef_Dictionary.xml` 的 `<DataElementCollection>` 内。


---

<a id="dataobject-generator"></a>
# YIGO DataObject 数据对象生成

## 概述

本 Skill 负责生成 YIGO 系统的**数据对象（DataObject）XML 配置**。DataObject 定义了表单的数据模型，包含表集合、列定义、表间关系、嵌入表和索引等。

> **核心原则**：Column 优先使用 `DataElementKey` 引用数据元素（由 [DataElement 生成](#dataelement-generator) 管理），通过 DataElement → Domain 链获取数据类型。如果DataObject是一个单独的文件，需要文件名与DataObject的Key相同。表标识必须要以E开头，如果已指定前缀，则在前缀前加上E

## XSD 参考文件

- 主文件：DataObject.xsd
- 详细定义：DataObjectDefine.xsd

## 数据模型关系

```
Column.DataElementKey → DataElement.Key → DataElement.DomainKey → Domain.Key
                         (字段元数据)         (关联域)              (数据域定义)
```

- **DataElement**（[DataElement 生成](#dataelement-generator)）：定义字段的 Key/Caption/DomainKey + 多语言标签
- **Domain**（[Domain 生成](#domain-generator)）：定义字段的 RefControlType/DataType/Length/Precision/Scale

## DataObject 完整结构

```xml
<DataObject Key="数据对象标识" Caption="名称" PrimaryType="Entity" 
            SecondaryType="Normal" PrimaryTableKey="主表Key">
    <!-- 1. 表集合 -->
    <TableCollection>
        <Table Key="表标识" Caption="表名称">
            <Column Key="列标识" Caption="列名称" DataElementKey="数据元素Key"/>
            <Column Key="Amount" DataType="Decimal" Precision="18" Scale="2"/>
            <TableFilter Type="Const">过滤条件</TableFilter>
            <ParameterCollection>
                <Parameter FieldKey="字段" TargetColumn="目标列" SourceType="Field"/>
            </ParameterCollection>
            <Statement Type="Formula">
                <![CDATA[SQL或公式]]>
            </Statement>
            <IndexCollection>
                <Index Key="idx1" Columns="col1,col2" IsUnique="true"/>
            </IndexCollection>
        </Table>
    </TableCollection>

    <!-- 2. 嵌入表集合 -->
    <EmbedTableCollection>
        <EmbedTable ObjectKey="对象标识" TableKeys="表1,表2"/>
    </EmbedTableCollection>
</DataObject>
```

## DataObject 属性

| 属性 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `Key` | string(50) | ✅ | 数据对象唯一标识 |
| `Caption` | string | ❌ | 显示名称 |
| `PrimaryType` | 枚举 | ❌ | 主类型：`Entity`（实体）/ `Template`（模板） |
| `SecondaryType` | 枚举 | ❌ | 辅助类型，见下表 |
| `PrimaryTableKey` | string(50) | ❌ | 主表标识 |
| `Version` | string | ❌ | 配置版本 |
| `NoPrefix` | string | ❌ | 单据编号前缀 |
| `DisplayFields` | string | ❌ | 字典显示字段 |
| `DropviewFields` | string | ❌ | 字典下拉框显示列 |
| `QueryFields` | string | ❌ | 字典模糊查询字段 |
| `MaintainDict` | Boolean | ❌ | 是否维护字典的 tleft tright |
| `IndexPrefix` | string | ❌ | 索引前缀 |
| `BrowserFormKey` | Formula | ❌ | 浏览表单标识 |
| `QueryFormKey` | Formula | ❌ | 查询表单标识 |
| `RelateObjectKey` | string(50) | ❌ | 关联的数据对象（View 用） |
| `IOProvider` | string | ❌ | IO 工厂类标识 |
| `CheckAfterLoad` | Boolean | ❌ | 权限检查在加载后还是加载前 |
| `LoadRightsType` | `Deny` | ❌ | 加载权限类型 |
| `MigrationUpdateStrategy` | 枚举 | ❌ | 迁移表更新策略（仅迁移表用） |
| `DictCacheCheckMode` | string | ❌ | 字典缓存检查策略（仅字典表单用） |

## SecondaryType 枚举值

| 值 | 说明 |
|----|------|
| `Normal` | 普通实体数据 |
| `Dict` | 字典数据 |
| `ChainDict` | 链式字典数据 |
| `CompDict` | 复合字典数据 |
| `Migration` | 迁移表 |

## Table（表）属性

| 属性 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `Key` | string(50) | ❌ | 表标识（持久化表 ≤ 30 字符） |
| `Caption` | string | ❌ | 表名称 |
| `TableMode` | `Detail` | ❌ | 表模式（明细表填 `Detail`，主表不填） |
| `PrimaryKey` | string | ❌ | 主键字段 |
| `Persist` | Boolean | ❌ | 是否持久化到数据库 |
| `SourceType` | 枚举 | ❌ | 来源类型：`Table`/`Query`/`Custom`/`Interface`/空 |
| `IndexPrefix` | string | ❌ | 索引前缀（明细表推荐配置） |
| `OrderBy` | string | ❌ | 排序方式 |
| `GroupBy` | string | ❌ | 分组方式 |
| `Formula` | Formula | ❌ | 表达式 |
| `Impl` | string | ❌ | 实现类 |
| `ParentKey` | string | ❌ | 父表标识 |
| `DBTableName` | string(30) | ❌ | 数据库表名（当与 Key 不同时） |
| `LazyLoad` | Boolean | ❌ | 是否延迟加载 |
| `RefreshFilter` | Boolean | ❌ | 是否刷新过滤条件 |

## Column（列）属性

### 推荐方式：引用 DataElementKey，如果DataElement中无定义，则新增数据元素

```xml
<Column Key="SchedulingIndicator" Caption="计划标识" Cache="true" DataElementKey="SchedulingIndicatorType"/>
```

| 属性 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `Key` | string(50) | ✅ | 列标识（持久化列 ≤ 30 字符） |
| `Caption` | string | ❌ | 列名称 |
| `DataElementKey` | string | ❌ | 引用的数据元素 Key（**推荐**） |
| `DefaultValue` | string | ❌ | 默认值 |
| `PrimaryKey` | Boolean | ❌ | 是否主键（实际使用的属性名） |
| `Cache` | Boolean | ❌ | 是否缓存（字典字段推荐 `true`） |
| `Persist` | Boolean | ❌ | 是否持久化 |
| `SupportI18n` | Boolean | ❌ | 是否支持国际化 |
| `SortType` | `Asc`/`Desc` | ❌ | 排序类型 |
| `ItemKey` | string(50) | ❌ | 字典项标识（仅不使用 DataElementKey 时） |
| `RefCol` | string | ❌ | 引用列 |
| `RefItemKeyCol` | string(50) | ❌ | 引用字典项列 |
| `CodeColumnKey` | string | ❌ | 编码列标识 |
| `DBColumnName` | string | ❌ | 数据库列名 |
| `Expand` | Boolean | ❌ | 是否扩展 |
| `NeedRights` | Boolean | ❌ | 是否需要权限 |
| `IgnoreSave` | Boolean | ❌ | 是否忽略保存 |
| `IgnoreQuery` | Boolean | ❌ | 是否忽略查询 |
| `AccessControl` | Boolean | ❌ | 访问控制 |


## 字典表单系统字段

字典表单（`SecondaryType="Dict"`）的主表**必须包含**以下系统字段（使用 DataElementKey 引用）：

```xml
<!-- ========== 系统字段（必要） ========== -->
<Column Key="OID" Caption="对象标识" DataElementKey="OID"/>
<Column Key="SOID" Caption="主对象标识" DataElementKey="SOID"/>
<Column Key="POID" Caption="父对象标识" DataElementKey="POID"/>
<Column Key="VERID" Caption="对象版本" DataElementKey="VERID"/>
<Column Key="DVERID" Caption="对象明细版本" DataElementKey="DVERID"/>
<Column Key="Enable" Caption="启用标记" DefaultValue="1" DataElementKey="Enable"/><!--字典才必要-->
<Column Key="TLeft" Cache="true" DataElementKey="TLeft"/><!--字典才必要-->
<Column Key="TRight" Cache="true" DataElementKey="TRight"/><!--字典才必要-->
<Column Key="NodeType" Caption="节点类型" DataElementKey="NodeType"/><!--字典才必要-->
<Column Key="ParentID" Caption="上级节点" DataElementKey="ParentID"/><!--字典才必要-->
<Column Key="Code" Caption="代码" Cache="true" DefaultValue="" DataElementKey="Code" PrimaryKey="true"/><!--字典才必要-->
<Column Key="Name" Caption="名称" Persist="false" Cache="true" DefaultValue="" SupportI18n="true" DataElementKey="Name"/><!--字典才必要-->
<Column Key="ClientID" Caption="集团" DataElementKey="ClientID"/>
<Column Key="Creator" Caption="创建人员" DataElementKey="Creator"/>
<Column Key="CreateTime" Caption="创建时间" DataElementKey="CreateTime"/>
<Column Key="CreateDate" Caption="制单日期" DataElementKey="CreateDate"/>
<Column Key="Modifier" Caption="修改人员" DataElementKey="Modifier"/>
<Column Key="ModifyTime" Caption="修改时间" DataElementKey="ModifyTime"/>
<Column Key="SystemVestKey" Caption="单据Key" DataElementKey="SystemVestKey"/>
```

字典表单的**明细表**系统字段：

```xml
<Column Key="OID" Caption="对象标识" DataElementKey="OID"/>
<Column Key="SOID" Caption="主对象标识" DataElementKey="SOID"/>
<Column Key="POID" Caption="父对象标识" DataElementKey="POID"/>
<Column Key="VERID" Caption="对象版本" DataElementKey="VERID"/>
<Column Key="DVERID" Caption="对象明细版本" DataElementKey="DVERID"/>
<Column Key="Sequence" Caption="序号" DataElementKey="Sequence"/>
```

## 校验规则

1. **持久化的表** Key 长度 ≤ 30 字符
2. **持久化的列** Key 长度 ≤ 30 字符，否则 ≤ 50 字符
3. **存在 DataElementKey 时**不能配置 `Precision`、`Scale`、`Length`（XSD 强制）
4. **迁移表专有属性**（`GroupType`、`SplitType`、`PeriodImpl`）只有 `SecondaryType='Migration'` 时允许配置
5. **MigrationUpdateStrategy** 只有迁移表（`SecondaryType='Migration'`）允许配置
6. **DictCacheCheckMode** 只有字典表单（`FormType='Dict'`）允许配置
7. **Column Key 唯一**：同一 Table 下的 Column Key 不能重复

## 使用示例

### 示例：字典数据对象

```xml
<DataObject Key="PM_Strategy" Caption="维护策略" PrimaryTableKey="EPM_Strategy" SecondaryType="Dict" PrimaryType="Entity" Version="1">
    <TableCollection>
        <Table Key="EPM_Strategy" Caption="维护策略主表">
            <!-- 系统字段 -->
            <Column Key="OID" Caption="对象标识" DataElementKey="OID"/>
            <Column Key="SOID" Caption="主对象标识" DataElementKey="SOID"/>
            <Column Key="POID" Caption="父对象标识" DataElementKey="POID"/>
            <Column Key="VERID" Caption="对象版本" DataElementKey="VERID"/>
            <Column Key="DVERID" Caption="对象明细版本" DataElementKey="DVERID"/>
            <Column Key="Enable" Caption="启用标记" DefaultValue="1" DataElementKey="Enable"/>
            <Column Key="TLeft" Cache="true" DataElementKey="TLeft"/>
            <Column Key="TRight" Cache="true" DataElementKey="TRight"/>
            <Column Key="NodeType" Caption="节点类型" DataElementKey="NodeType"/>
            <Column Key="ParentID" Caption="上级节点" DataElementKey="ParentID"/>
            <Column Key="Code" Caption="代码" Cache="true" DefaultValue="" DataElementKey="Code" PrimaryKey="true"/>
            <Column Key="Name" Caption="名称" Persist="false" Cache="true" DefaultValue="" SupportI18n="true" DataElementKey="Name"/>
            <Column Key="ClientID" Caption="集团" DataElementKey="ClientID"/>
            <Column Key="Creator" Caption="创建人员" DataElementKey="Creator"/>
            <Column Key="CreateTime" Caption="创建时间" DataElementKey="CreateTime"/>
            <Column Key="CreateDate" Caption="制单日期" DataElementKey="CreateDate"/>
            <Column Key="Modifier" Caption="修改人员" DataElementKey="Modifier"/>
            <Column Key="ModifyTime" Caption="修改时间" DataElementKey="ModifyTime"/>
            <!-- 业务字段 -->
            <Column Key="Notes" Caption="备注" DefaultValue="" DataElementKey="Notes"/>
            <Column Key="SchedulingIndicator" Caption="计划标识" Cache="true" DataElementKey="SchedulingIndicatorType"/>
            <Column Key="StrategyUnitID" Caption="策略单位" Cache="true" DataElementKey="CycleUnitID"/>
            <Column Key="CallHorizon" Caption="调用期" Cache="true" DataElementKey="CallHorizon"/>
            <Column Key="SystemVestKey" Caption="单据Key" DataElementKey="SystemVestKey"/>
        </Table>
        <Table Key="EPM_StrategyDtl" Caption="维护策略明细" TableMode="Detail" IndexPrefix="EPM_StrategyDtl">
            <!-- 明细系统字段 -->
            <Column Key="OID" Caption="对象标识" DataElementKey="OID"/>
            <Column Key="SOID" Caption="主对象标识" DataElementKey="SOID"/>
            <Column Key="POID" Caption="父对象标识" DataElementKey="POID"/>
            <Column Key="VERID" Caption="对象版本" DataElementKey="VERID"/>
            <Column Key="DVERID" Caption="对象明细版本" DataElementKey="DVERID"/>
            <Column Key="Sequence" Caption="序号" DataElementKey="Sequence"/>
            <!-- 业务字段 -->
            <Column Key="CycleNotes" Caption="周期文本" DefaultValue="" DataElementKey="CycleNotes"/>
            <Column Key="CycleLength" Caption="周期长度" DataElementKey="PM_CycleLength"/>
            <Column Key="PackageNo" Caption="数据包" DataElementKey="PackageNo" PrimaryKey="true"/>
        </Table>
    </TableCollection>
</DataObject>
```

## 与其他 Skill 的配合

| 配合 Skill | 关系说明 |
|------------|----------|
| [DataElement 生成](#dataelement-generator) | Column 的 `DataElementKey` 引用 DataElement 定义 → 需确保被引用的 DataElement 存在于对应的 `DataElementDef_{Type}.xml` 中 |
| [Domain 生成](#domain-generator) | DataElement 的 `DomainKey` 引用 Domain 定义 → 需确保被引用的 Domain 存在于对应的 `DomainDef_{Type}.xml` 中 |
| [Form 脚手架](#form-scaffold) | DataObject 嵌套在 Form XML 的 `<DataSource>` 下 |
| [UI 控件生成](#control-generator) | DataObject 的 Table/Column 供 UI 控件做 DataBinding（`TableKey` + `ColumnKey`） |
| [Grid 表格生成](#grid-generator) | Grid 列绑定 DataObject 的 Column |

### 生成流程

当生成一个完整的表单数据模型时，需按以下顺序协作：

1. **Domain**（[Domain 生成](#domain-generator)）→ 定义或复用数据域，追加到 `DomainDef_{Type}.xml`
2. **DataElement**（[DataElement 生成](#dataelement-generator)）→ 定义字段元数据，追加到 `DataElementDef_{Type}.xml`
3. **DataObject**（本 Skill）→ 构建数据对象，Column 通过 `DataElementKey` 引用上述定义


---

<!-- =========================================================== -->
# 第五部分：Form 表单层
<!-- =========================================================== -->

<a id="form-scaffold"></a>
# YIGO Form 脚手架生成

## 概述

本 Skill 负责生成 YIGO Form XML 的**顶层骨架结构**。Form 是 YIGO 系统中窗口配置的根对象，包含数据源、操作集合、脚本集合、窗体等核心子元素。文件名与Form的Key要相同

## CDATA 规约

> **所有 XML 元素内的表达式（公式）都必须用 `<![CDATA[]]>` 包裹**，避免 `&`, `<`, `>` 等特殊字符导致 XML 解析错误。

```xml
<!-- 正确 -->
<OnLoad><![CDATA[Macro_LoadObject()]]></OnLoad>
<Macro Key="m"><![CDATA[IIF(a>0 && b<10, true, false)]]></Macro>
<CheckRule><![CDATA[IIF(Code=='', '请输入代码', true)]]></CheckRule>

<!-- 错误 -->
<OnLoad>IIF(a>0 && b<10, true, false)</OnLoad>
```

**适用范围**：`OnLoad`, `OnClose`, `OnPostShow`, `Action`, `ExceptionHandler`, `Macro` 内容, `CheckRule`, `ValueChanged`, `DefaultFormulaValue`, `OnClick`, `RowDblClick`, `Statement`, `FormulaItems`, `OnRowDelete` 等一切公式体。

## XSD 参考文件

- 主文件：Form.xsd
- 枚举定义：FormDefine.xsd
- 参考表单：参考表单目录

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
    
    <!-- 6. 事件钩子（可选，内容用 CDATA 包裹） -->
    <OnLoad><![CDATA[公式内容]]></OnLoad>
    <OnClose><![CDATA[公式内容]]></OnClose>
    <OnPostShow><![CDATA[公式内容]]></OnPostShow>
    
    <!-- 7. 宏公式集合（可选） -->
    <MacroCollection>
        <Macro Key="宏标识" Args="参数"><![CDATA[公式内容]]></Macro>
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

## 命名规范

1.

## 表单模板

根据需要生成的表单类型，读取对应模板文件获取完整骨架结构：

| 模板 | 文件 | 适用场景 |
|------|------|---------|
| 字典表单 | [字典表单模板](#template-dict-form) | `FormType="Dict"` + SplitPanel（表头 + 明细表）+ 系统信息页签 |
| 后台配置表 | [后台配置表模板](#template-backend-config) | `FormType="Entity"` + 纯 Detail Grid，无表头，OnLoad 加载 |
| 单界面报表 | [单界面报表模板](#template-single-report) | SplitPanel（条件区 + 结果 Grid），控件带 `Condition` 子元素 |
| 双表单报表 | [双表单报表模板](#template-dual-report) | 条件表单 + 结果表单，`ERPShowModal` 弹出条件，输入条件后确认加载数据 |



## 与其他 Skill 的配合

- **Body > Block** 内的面板/控件 → 使用 [面板布局](#panel-layout) 和 [UI 控件生成](#control-generator)
- **DataSource > DataObject** → 使用 [DataObject 生成](#dataobject-generator)
- **OperationCollection** → 使用 [操作与脚本](#operation-script)
- **OnLoad/OnClose 等事件内容** → 使用 [表达式书写](#expression-writer)
- **抬头控件布局** → 优先使用 `GridLayoutPanel`（X/Y 精确定位），参考 [面板布局](#panel-layout)


### 表单模板详细内容

<a id="template-dict-form"></a>
#### 模板 1：字典表单

# 模板：字典表单

典型结构：`FormType="Dict"` + SplitPanel（上 GridLayoutPanel 表头 + 下 Grid 明细） + TabPanel 系统信息页签。

```xml
<Form Key="{字典Key}" Caption="{字典名称}" FormType="Dict" Version="1">
    <DataSource>
        <DataObject Key="{字典Key}" Caption="{名称}" PrimaryTableKey="{主表Key}" SecondaryType="Dict" PrimaryType="Entity">
            <!-- 由 yigo-dataobject-generator 生成 -->
        </DataObject>
    </DataSource>
    <OperationCollection>
        <!-- 字典表单标准操作集 -->
        <Operation Key="DicNew" Caption="新增" RefKey="DicNew" TCode="GetEntryTCode()" Activity="01"/>
        <Operation Key="DicCopyNew" Caption="复制新增" RefKey="DicCopyNew" TCode="GetEntryTCode()" Activity="01"/>
        <Operation Key="DicModify" Caption="修改" RefKey="DicModify" TCode="GetEntryTCode()" Activity="02"/>
        <Operation Key="DicSave" Caption="保存" RefKey="DicSave"/>
        <Operation Key="DicCancel" Caption="取消" RefKey="DicCancel"/>
        <Operation Key="DicEnabled" Caption="启用" RefKey="DicEnabled" Activity="Y02"/>
        <Operation Key="DicDisabled" Caption="停用" RefKey="DicDisabled" Activity="Y01"/>
        <Operation Key="DicInvalid" Caption="作废" RefKey="DicInvalid" Activity="Y03"/>
        <Operation Key="DicDelete" Caption="删除" RefKey="DicDelete" Activity="06"/>
        <Operation Key="DicRefresh" Caption="刷新" RefKey="DicRefresh"/>
        <Operation Key="Lang" Caption="多语言" RefKey="Lang" Activity="Y10"/>
        <Operation Key="ShowDataLog" Caption="查看数据日志" RefKey="ShowDataLog" Activity="Y08"/>
        <Operation Key="DicExit" Caption="关闭" RefKey="DicExit"/>
    </OperationCollection>
    <Body>
        <Block>
            <FlexFlowLayoutPanel Key="root">
                <TabPanel Key="body" Height="100%">
                    <SplitPanel Key="BasicInformation" Caption="基本信息" Orientation="Vertical">
                        <!-- 表头：GridLayoutPanel（X/Y 精确定位） -->
                        <GridLayoutPanel Key="body_basic" Padding="8px" OverflowY="Auto" TopPadding="24px">
                            <!-- 由 yigo-control-generator 生成控件 -->
                            <RowDefCollection RowGap="24">
                                <RowDef Height="32px"/>
                                <!-- 按需添加行 -->
                            </RowDefCollection>
                            <ColumnDefCollection ColumnGap="16">
                                <ColumnDef Width="25%"/>
                                <ColumnDef Width="25%"/>
                                <ColumnDef Width="25%"/>
                                <ColumnDef Width="25%"/>
                                <!-- 按需添加列，一般是三列或四列 -->
                            </ColumnDefCollection>
                        </GridLayoutPanel>
                        <!-- 明细 Grid -->
                        <Grid Key="{DtlGridKey}" Caption="{明细名称}" SerialSeq="true" Padding="8px">
                            <!-- 由 yigo-grid-generator 生成 -->
                        </Grid>
                        <SplitSize Size="425px"/><!-- 根据行高和行数 生成 -->
                        <SplitSize Size="100%"/>
                    </SplitPanel>
                    <!-- 系统信息页签 -->
                    <FlexFlowLayoutPanel Key="BodySystem" Caption="系统信息" Padding="8px">
                        <Embed Key="BodySystemEmbed" FormKey="BodySystemDictForm" RootKey="SystemInfoPanel">
                            <Var Key="SystemInfoTableKey" Value="{主表Key}"/>
                        </Embed>
                    </FlexFlowLayoutPanel>
                </TabPanel>
            </FlexFlowLayoutPanel>
        </Block>
    </Body>
    <MacroCollection>
        <!-- 由 yigo-operation-script 生成 -->
    </MacroCollection>
</Form>
```


<a id="template-backend-config"></a>
#### 模板 2：后台配置表

# 模板：后台配置单明细表

典型结构：`FormType="Entity"` + 只有 Detail Grid（无主表表头），ToolBar + GridLayoutPanel 包裹 Grid，OnLoad 加载数据。

```xml
<Form Key="{ConfigKey}" Caption="{配置名称}" FormType="Entity" InitState="Default" DeliveryClass="C">
    <DataSource>
        <DataObject Key="{ConfigKey}" Caption="{名称}" PrimaryType="Entity">
            <TableCollection>
                <!-- 只有一张 Detail 表，无主表 -->
                <Table Key="{DetailTableKey}" Caption="{明细名称}" TableMode="Detail">
                    <!-- 由 yigo-dataobject-generator 生成 Column -->
                </Table>
            </TableCollection>
        </DataObject>
    </DataSource>
    <OperationCollection>
        <Operation Key="BillEdit" Caption="修改" RefKey="BillEdit" TCode="'{TCode}'" Activity="02"/>
        <Operation Key="BillSave" Caption="保存" RefKey="BillSave"/>
        <Operation Key="BillCancel" Caption="取消" RefKey="BillCancel"/>
        <Operation Key="ShowDataLog" Caption="查看数据日志" RefKey="ShowDataLog"/>
        <Operation Key="PositionCursor" Caption="定位" RefKey="PositionCursor"/>
        <Operation Key="UIClose" Caption="关闭" RefKey="UIClose"/>
    </OperationCollection>
    <Body>
        <Block>
            <FlexFlowLayoutPanel Key="root">
                <ToolBar Key="ToolBar1" Caption="ToolBar1" Height="pref">
                    <ToolBarItemCollection/>
                </ToolBar>
                <GridLayoutPanel Key="main_container" Height="100%" OverflowY="Auto">
                    <Grid Key="{GridKey}" Caption="{名称}" DefaultFitWidth="true" SerialSeq="true" X="0" Y="0" Padding="8px">
                        <!-- 由 yigo-grid-generator 生成 -->
                    </Grid>
                    <RowDefCollection RowGap="24">
                        <RowDef Height="100%"/>
                    </RowDefCollection>
                    <ColumnDefCollection ColumnGap="16">
                        <ColumnDef Width="100%"/>
                    </ColumnDefCollection>
                </GridLayoutPanel>
            </FlexFlowLayoutPanel>
        </Block>
    </Body>
    <OnLoad><![CDATA[Macro_LoadObject()]]></OnLoad>
</Form>
```


<a id="template-single-report"></a>
#### 模板 3：单界面报表

# 模板：单界面报表（条件 + 结果表格）

典型结构：SplitPanel（上 GridLayoutPanel 条件区 + 下 Grid 结果区），条件控件带 `Condition` 子元素。

```xml
<Form Key="{ReportKey}" Caption="{报表名称}" FormType="Entity">
    <DataSource>
        <DataObject Key="{ReportKey}" Caption="{名称}" PrimaryType="Entity">
            <TableCollection>
                <!-- 结果表：SourceType="Query", Persist="false" -->
                <Table Key="{ResultTableKey}" Caption="{结果}" TableMode="Detail" SourceType="Query" Persist="false">
                    <Statement Type="Formula"><![CDATA['SELECT ... FROM ... ' + Macro_GetWhere()]]></Statement>
                </Table>
                <!-- 条件表：非持久化 -->
                <Table Key="{CondTableKey}" Caption="{条件}" Persist="false">
                    <!-- 条件列（Persist="false", IgnoreQuery="true"） -->
                </Table>
            </TableCollection>
        </DataObject>
    </DataSource>
    <OperationCollection>
        <Operation Key="Query" Caption="查询" RefKey="Query"/>
        <OperationCollection Key="NewPrint" Caption="打印" SelfDisable="true">
            <Operation Key="NewPrintDefault" Caption="默认模板打印" RefKey="NewPrintDefault"/>
            <Operation Key="NewPrintSelect" Caption="其他模板选择" RefKey="NewPrintSelect"/>
            <Operation Key="ManagePrint" Caption="打印模板管理" RefKey="ManagePrint"/>
        </OperationCollection>
        <OperationCollection Key="NewPrePrint" Caption="打印预览" SelfDisable="true">
            <Operation Key="NewPrePrintDefault" Caption="默认模板预览" RefKey="NewPrePrintDefault"/>
            <Operation Key="NewPrePrintSelect" Caption="其他模板选择" RefKey="NewPrePrintSelect"/>
        </OperationCollection>
        <Operation Key="ERPExportExcel" Caption="导出" RefKey="ERPExportExcel"/>
        <Operation Key="UIClose" Caption="关闭" RefKey="UIClose"/>
    </OperationCollection>
    <Body PopHeight="700px" PopWidth="635px">
        <Block>
            <FlexFlowLayoutPanel Key="root">
                <ToolBar Key="ToolBar1" Caption="ToolBar1" Height="pref">
                    <ToolBarItemCollection/>
                </ToolBar>
                <SplitPanel Key="main_container" Orientation="Vertical" Height="100%">
                    <!-- 条件区：GridLayoutPanel + 控件带 Condition -->
                    <GridLayoutPanel Key="{CondPanelKey}" Caption="查询条件" Padding="8px" OverflowY="Auto" TopPadding="24px">
                        <!-- 条件控件示例 -->
                        <!--
                        <Dict Key="Cond_Field" Caption="字段" ItemKey="...">
                            <DataBinding TableKey="{CondTableKey}" ColumnKey="Cond_Field"/>
                            <Condition ColumnKey="Field" TableKey="{ResultTableKey}" CondSign="=" UseAdvancedQuery="true"/>
                        </Dict>
                        -->
                        <RowDefCollection RowGap="24">
                            <RowDef Height="32px"/>
                        </RowDefCollection>
                        <ColumnDefCollection ColumnGap="16">
                            <ColumnDef Width="33%"/>
                            <ColumnDef Width="33%"/>
                            <ColumnDef Width="34%"/>
                        </ColumnDefCollection>
                    </GridLayoutPanel>
                    <!-- 结果区：只读 Grid -->
                    <Grid Key="{ResultGridKey}" Caption="查询结果" Enable="false" SerialSeq="true" DisabledOption="delete" Padding="8px" PageLoadType="DB">
                        <!-- 由 yigo-grid-generator 生成 -->
                    </Grid>
                    <SplitSize Size="125px"/>
                    <SplitSize Size="100%"/>
                </SplitPanel>
            </FlexFlowLayoutPanel>
        </Block>
    </Body>
    <OnLoad><![CDATA[Macro_LoadObject()]]></OnLoad>
</Form>
```


<a id="template-dual-report"></a>
#### 模板 4：双表单报表

# 模板：双表单报表（条件表单 + 结果表单）

由**条件选择表单**和**结果展示表单**两个 Form 组成：
- 结果表单（parent）在 `OnLoad` 中通过 `ERPShowModal` 弹出条件表单
- 结果表单有"重新选择"操作按钮，再次弹出条件表单
- 条件表单通过 `DealCondition(true);parent.Macro_LoadObject()` 将条件传递给结果表单

## 结果表单

```xml
<Form Key="{ResultFormKey}" FormType="Entity" FormulaCaption="{动态标题公式}" InitState="Default">
    <DataSource>
        <DataObject Key="{ResultDOKey}" Caption="{名称}" PrimaryType="Entity">
            <TableCollection>
                <Table Key="{ResultTableKey}" TableMode="Detail" SourceType="Query" Persist="false">
                    <Statement Type="Formula"><![CDATA['SELECT ... FROM ...']]></Statement>
                </Table>
            </TableCollection>
        </DataObject>
    </DataSource>
    <OperationCollection>
        <!-- 重新选择：再次弹出条件表单 -->
        <Operation Key="Filter" Caption="重新选择" Enable="true">
            <Action><![CDATA[ERPShowModal('{CondFormKey}')]]></Action>
        </Operation>
        <Operation Key="Refresh" Caption="刷新" RefKey="Refresh"/>
        <OperationCollection Key="NewPrint" Caption="打印" SelfDisable="true">
            <Operation Key="NewPrintDefault" Caption="默认模板打印" RefKey="NewPrintDefault" Activity="04"/>
            <Operation Key="NewPrintSelect" Caption="其他模板选择" RefKey="NewPrintSelect" Activity="04"/>
        </OperationCollection>
        <Operation Key="ERPExportExcel" Caption="导出" RefKey="ERPExportExcel" Activity="Y11"/>
        <Operation Key="UIClose" Caption="关闭" RefKey="UIClose"/>
    </OperationCollection>
    <Body>
        <Block>
            <FlexFlowLayoutPanel Key="root">
                <ToolBar Key="ToolBar1" Caption="ToolBar1" Height="pref">
                    <ToolBarItemCollection/>
                </ToolBar>
                <GridLayoutPanel Key="main_container" Height="100%" OverflowY="Auto">
                    <Grid Key="{ResultGridKey}" PageLoadType="DB" SerialSeq="true" X="0" Y="0" Padding="8px">
                        <!-- 由 yigo-grid-generator 生成 -->
                    </Grid>
                    <RowDefCollection RowGap="24">
                        <RowDef Height="100%"/>
                    </RowDefCollection>
                    <ColumnDefCollection ColumnGap="16">
                        <ColumnDef Width="100%"/>
                    </ColumnDefCollection>
                </GridLayoutPanel>
            </FlexFlowLayoutPanel>
        </Block>
    </Body>
    <!-- OnLoad 弹出条件选择界面 -->
    <OnLoad><![CDATA[ERPShowModal('{CondFormKey}')]]></OnLoad>
</Form>
```

## 条件表单

```xml
<Form Key="{CondFormKey}" Caption="{条件界面名称}">
    <DataSource>
        <DataObject Key="{CondFormKey}" Caption="{名称}" PrimaryType="Entity">
            <TableCollection>
                <!-- 非持久化条件表 -->
                <Table Key="{CondTableKey}" Persist="false">
                    <!-- 条件字段：Persist="false", IgnoreQuery="true" -->
                </Table>
            </TableCollection>
        </DataObject>
    </DataSource>
    <!-- 条件表单无 OperationCollection，按钮在 Body 内 -->
    <Body PopHeight="800px" PopWidth="600px">
        <Block>
            <FlexFlowLayoutPanel Key="root">
                <!-- 条件区域 -->
                <GridLayoutPanel Key="main_container" Height="100%" Padding="8px" OverflowY="Auto">
                    <!-- 分组标题 -->
                    <Label Key="HeadLabel" Caption="选择条件" X="0" Y="0" Class="erp-group-title" XSpan="2">
                        <DataBinding/>
                    </Label>
                    <!-- 条件控件 + Condition 子元素 -->
                    <!--
                    <Dict Key="FieldID" Caption="字段" X="0" Y="1" ItemKey="..." OneTimeCompute="true">
                        <DataBinding TableKey="{CondTableKey}" ColumnKey="FieldID"/>
                        <Condition ColumnKey="FieldID" TableKey="{ResultTableKey}" CondSign="=" UseAdvancedQuery="true"/>
                    </Dict>
                    -->
                    <RowDefCollection RowGap="24">
                        <RowDef Height="32px"/>
                    </RowDefCollection>
                    <ColumnDefCollection ColumnGap="16">
                        <ColumnDef Width="50%"/>
                        <ColumnDef Width="50%"/>
                    </ColumnDefCollection>
                </GridLayoutPanel>
                <!-- 按钮区域：确定/取消/查询变式 -->
                <GridLayoutPanel Key="ButtonPanel" Height="100px" Padding="8px" TopPadding="24px">
                    <Button Key="OK" Caption="确定" X="3" Y="1" Type="Primary">
                        <OnClick><![CDATA[UICheck();
DealCondition(true);
parent.LoadData();
Close('OK');]]></OnClick>
                    </Button>
                    <Button Key="Cancel" Caption="取消" X="2" Y="1" Type="Normal">
                        <OnClick><![CDATA[Close();]]></OnClick>
                    </Button>
                    <Embed Key="UserFavorite" Caption="查询变式" FormKey="V_Favorite_Impl" RootKey="Favorite_ImplFavoriteGridLayoutPanel" IncludeDataTable="false" X="0" Y="0" YSpan="2"/>
                    <RowDefCollection>
                        <RowDef Height="32px"/>
                        <RowDef Height="32px"/>
                    </RowDefCollection>
                    <ColumnDefCollection ColumnGap="16">
                        <ColumnDef Width="340px"/>
                        <ColumnDef Width="100%"/>
                        <ColumnDef Width="80px"/>
                        <ColumnDef Width="80px"/>
                    </ColumnDefCollection>
                </GridLayoutPanel>
            </FlexFlowLayoutPanel>
        </Block>
    </Body>
</Form>
```


---

<!-- =========================================================== -->
# 第六部分：UI 层（面板 → 控件 → Grid）
<!-- =========================================================== -->

<a id="panel-layout"></a>
# YIGO 面板与布局生成

## 概述

本 Skill 负责生成 YIGO Form XML 中 **Body > Block** 下的面板和布局结构。YIGO 支持 18 种面板类型，面板之间可以嵌套组合，面板内可以放置 UI 控件或子面板。

> **抬头控件优先使用 `GridLayoutPanel`**（X/Y 精确定位），而不是 `FlexFlowLayoutPanel` 或 `FlexGridLayoutPanel`。参考 PM_Strategy.xml 和 Cond_PM_EquipmentQuery.xml 的实际用法。

## XSD 参考文件

- 面板定义：PanelDefine.xsd
- 网格面板：GridLayoutPanel.xsd
- 面板属性：PanelAttributeGroupDefinition.xsd

## Block 容器

Block 是 Body 下的直接子元素，用于组织面板和控件。

```xml
<Body>
    <Block Key="blockKey" Caption="区域名称">
        <!-- 面板或 Format 元素 -->
    </Block>
</Body>
```

## 面板类型速查

| 面板类型 | 说明 | 可包含控件 | 可包含子面板 |
|----------|------|-----------|-------------|
| `GridLayoutPanel` | 网格布局（行列定义） | ✅ | ✅ |
| `FlexFlowLayoutPanel` | 弹性流式布局（**最常用**） | ✅ | ✅ |
| `FlexGridLayoutPanel` | 弹性网格布局 | ✅ | ✅ |
| `FlowLayoutPanel` | 流式布局 | ✅ | ✅ |
| `BorderLayoutPanel` | 边框布局（上下左右中） | ✅ | ✅ |
| `TabPanel` | 分页面板 | ✅ | ✅ |
| `SplitPanel` | 分割面板 | ✅ | ✅ |
| `Container` | 容器（可嵌入其他表单） | ❌ | ✅ |
| `SubDetail` | 嵌入子明细 | ❌ | ✅ |
| `Grid` | 表格控件 | ❌ | ❌ |
| `DictView` | 字典视图 | ❌ | ❌ |
| `Embed` | 嵌入其他表单 | ❌ | ❌ |
| `LinearLayoutPanel` | 线性布局 | ❌ | ✅ |
| `PopView` | 弹出视图 | ❌ | ✅ |
| `HoverButton` | 悬浮按钮 | ❌ | ❌ |
| `TableView` | 表格视图 | ❌ | ❌ |
| `TabGroup` | 页签组 | ❌ | ✅ |
| `Chart` | 图表 | ❌ | ❌ |

## 公共属性

所有面板共享以下属性组：

### yigo-Key-Caption（标识与名称）

| 属性 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `Key` | string | ✅ | 面板唯一标识 |
| `Caption` | string | ❌ | 显示名称 |

### yigo-Visible-Enable（可见性与可用性）

| 属性 | 类型 | 说明 |
|------|------|------|
| `Visible` | Boolean/Formula | 是否可见 |
| `Enable` | Boolean/Formula | 是否可用 |
| `VisibleDependency` | string | 可见性依赖项 |
| `EnableDependency` | string | 可用性依赖项 |

### yigo-Component-XY（网格布局定位）

| 属性 | 类型 | 说明 |
|------|------|------|
| `X` | int(≥0) | X 坐标（列号，从 0 开始） |
| `Y` | int(≥0) | Y 坐标（行号，从 0 开始） |
| `XSpan` | int(≥0) | 列跨度 |
| `YSpan` | int(≥0) | 行跨度 |

### yigo-Panel-layout-collection（面板布局属性）

面板在父容器中的尺寸和对齐属性。参见 `yigo-Control-layout-collection`。

---

## 常用面板详细说明

### 1. FlexFlowLayoutPanel（弹性流式布局） ⭐ 最常用

```xml
<FlexFlowLayoutPanel Key="headerPanel" Caption="表头区域">
    <Format>...</Format>           <!-- 可选：格式 -->
    <ToolBar Key="tb" Caption="工具栏" />  <!-- 可选：工具栏 -->
    <!-- 控件和子面板混合排列 -->
    <TextEditor Key="PONo" Caption="订单编号" />
    <Dict Key="SupplierID" Caption="供应商" />
</FlexFlowLayoutPanel>
```

### 2. FlexGridLayoutPanel（弹性网格布局）

```xml
<FlexGridLayoutPanel Key="gridPanel" Caption="网格区域" 
                     ColumnCount="3" ColumnGap="10" RowGap="8" RowHeight="32">
    <TextEditor Key="Field1" Caption="字段1" />
    <TextEditor Key="Field2" Caption="字段2" />
    <TextEditor Key="Field3" Caption="字段3" />
</FlexGridLayoutPanel>
```

| 专有属性 | 说明 |
|----------|------|
| `ColumnCount` | 列数 |
| `ColumnGap` | 列间距 |
| `RowGap` | 行间距 |
| `RowHeight` | 行高 |

### 3. GridLayoutPanel（网格布局面板）

```xml
<GridLayoutPanel Key="gridLPanel" Caption="精确布局">
    <RowDefCollection RowHeight="32" RowGap="5">
        <RowDef Height="32" />
        <RowDef Height="32" />
    </RowDefCollection>
    <ColumnDefCollection ColumnGap="10">
        <ColumnDef Width="120" />
        <ColumnDef Width="200" />
    </ColumnDefCollection>
    <TextEditor Key="f1" Caption="字段1" X="0" Y="0" />
    <TextEditor Key="f2" Caption="字段2" X="1" Y="0" />
</GridLayoutPanel>
```

### 4. TabPanel（分页面板）

```xml
<TabPanel Key="tabMain" Caption="主页签" TabPosition="Top">
    <FlexFlowLayoutPanel Key="tab1" Caption="基本信息">
        <!-- 页签 1 的内容 -->
    </FlexFlowLayoutPanel>
    <FlexFlowLayoutPanel Key="tab2" Caption="附加信息">
        <!-- 页签 2 的内容 -->
    </FlexFlowLayoutPanel>
    <ItemChanged />  <!-- 可选：页签切换事件 -->
</TabPanel>
```

| 专有属性 | 说明 |
|----------|------|
| `TabPosition` | Tab 页位置：`Top`/`Bottom`/`Left`/`Right` |

### 5. SplitPanel（分割面板）

```xml
<SplitPanel Key="splitMain" Caption="分割布局" Orientation="Horizontal">
    <FlexFlowLayoutPanel Key="leftPanel" Caption="左侧" />
    <FlexFlowLayoutPanel Key="rightPanel" Caption="右侧" />
    <SplitSize MinWidth="200" MaxWidth="500" />
</SplitPanel>
```

| 专有属性 | 说明 |
|----------|------|
| `Orientation` | 方向：`Horizontal`/`Vertical` |

### 6. Container（容器 — 嵌入表单）

```xml
<Container Key="detailContainer" Caption="明细容器" 
           DefaultFormKey="PurchaseOrderDetail" Style="Normal">
</Container>
```

| 专有属性 | 说明 |
|----------|------|
| `Style` | 容器样式 |
| `MergeOperation` | 是否合并操作至父界面 |
| `DefaultFormKey` | 默认打开的表单标识 |
| `FormulaFormKey` | 公式表单标识（优先级更高） |

### 7. SubDetail（嵌入子明细）

```xml
<SubDetail Key="subDtl" Caption="嵌入子明细" BindingGridKey="gridDtl">
    <FlexFlowLayoutPanel Key="dtlPanel" Caption="明细面板">
        <!-- 子明细的控件 -->
    </FlexFlowLayoutPanel>
</SubDetail>
```

| 专有属性 | 说明 |
|----------|------|
| `BindingGridKey` | 关联表格的 Key |

### 8. ToolBar（工具栏）

```xml
<ToolBar Key="toolbar1" Caption="工具栏">
    <ToolBarItemCollection>按钮1,按钮2</ToolBarItemCollection>
</ToolBar>
```

### 9. DictView（字典视图）

```xml
<DictView Key="dictView1" Caption="字典视图" FormulaItemKey="公式" LoadType="Full" PageRowCount="20">
    <RowClick>点击事件公式</RowClick>
    <RowDblClick>双击事件公式</RowDblClick>
    <DictViewColumnCollection>...</DictViewColumnCollection>
</DictView>
```

---

## 典型布局模式

### 模式 1：标准单据表单（表头 + 明细）

```xml
<Body>
    <Block>
        <FlexFlowLayoutPanel Key="root">
            <SplitPanel Key="mainSplit" Orientation="Vertical">
                <!-- 表头区域：优先使用 GridLayoutPanel -->
                <GridLayoutPanel Key="headerPanel" Caption="表头" Padding="8px" OverflowY="Auto" TopPadding="24px">
                    <!-- 表头控件由 yigo-control-generator 生成，用 X/Y 定位 -->
                    <RowDefCollection RowGap="24">
                        <RowDef Height="32px"/>
                    </RowDefCollection>
                    <ColumnDefCollection ColumnGap="16">
                        <ColumnDef Width="190px"/>
                        <ColumnDef Width="30px"/>
                        <ColumnDef Width="190px"/>
                        <ColumnDef Width="30px"/>
                    </ColumnDefCollection>
                </GridLayoutPanel>
                <!-- 明细区域 -->
                <Grid Key="gridDetail" Padding="8px">
                    <!-- 表格由 yigo-grid-generator 生成 -->
                </Grid>
                <SplitSize Size="425px"/>
                <SplitSize Size="100%"/>
            </SplitPanel>
        </FlexFlowLayoutPanel>
    </Block>
</Body>
```

### 模式 2：带页签的表单

```xml
<Body>
    <Block Key="mainBlock">
        <FlexFlowLayoutPanel Key="headerPanel" Caption="基本信息">
            <!-- 表头控件 -->
        </FlexFlowLayoutPanel>
        <TabPanel Key="tabDetail" TabPosition="Top">
            <FlexFlowLayoutPanel Key="tab1" Caption="明细1">
                <Grid Key="grid1">...</Grid>
            </FlexFlowLayoutPanel>
            <FlexFlowLayoutPanel Key="tab2" Caption="明细2">
                <Grid Key="grid2">...</Grid>
            </FlexFlowLayoutPanel>
        </TabPanel>
    </Block>
</Body>
```

### 模式 3：叙时簿（View 表单）

```xml
<Body>
    <Block Key="mainBlock">
        <FlexFlowLayoutPanel Key="queryPanel" Caption="查询条件">
            <!-- 查询条件控件 -->
        </FlexFlowLayoutPanel>
        <Grid Key="gridList">
            <!-- 数据列表 Grid -->
        </Grid>
    </Block>
</Body>
```

## 与其他 Skill 的配合

- Panel 作为 **容器**，放置在 [Form 脚手架](#form-scaffold) 生成的 Body > Block 内
- Panel 内放置 **控件** → 使用 [UI 控件生成](#control-generator)
- Panel 内放置 **Grid 表格** → 使用 [Grid 表格生成](#grid-generator)
- Panel 的 Key 在整个 Block 内必须唯一


---

<a id="control-generator"></a>
# YIGO UI 控件生成

## 概述

本 Skill 负责生成 YIGO Form XML 中的 **UI 控件**。YIGO 支持约 30 种控件类型，每种控件都有特定的属性和子元素，字典控件的ItemKey对应SAP中域的值表。控件放置在面板（Panel）内。

> **所有控件内的表达式（公式）都必须用 `<![CDATA[]]>` 包裹**，包括 `OnClick`、`KeyEnter`、`CheckRule`、`ValueChanged`、`DefaultFormulaValue` 等。

## XSD 参考文件

- 控件定义：BaseControlDefinition.xsd
- 子元素定义：BaseControlChildElementDefinition.xsd
- 属性组定义：ControlAttributeGroupDefinition.xsd

## 控件分类速查

| 类别 | 控件 | 元素名 |
|------|------|--------|
| **文本输入** | 文本编辑器 | `TextEditor` |
| | 多行文本 | `TextArea` |
| | 密码编辑器 | `PasswordEditor` |
| | 富文本编辑器 | `RichEditor` |
| **数值输入** | 数字编辑器 | `NumberEditor` |
| **选择控件** | 下拉框 | `ComboBox` |
| | 复选框 | `CheckBox` |
| | 复选列表 | `CheckListBox` |
| | 单选按钮 | `RadioButton` |
| **字典控件** | 字典选择 | `Dict` |
| | 动态字典 | `DynamicDict` |
| **日期时间** | 日期选择器 | `DatePicker` |
| | UTC 日期 | `UTCDatePicker` |
| | 月份选择器 | `MonthPicker` |
| | 时间选择器 | `TimePicker` |
| **按钮类** | 按钮 | `Button` |
| | 文本按钮 | `TextButton` |
| | 下拉按钮 | `DropdownButton` |
| | 超链接 | `HyperLink` |
| **展示类** | 标签 | `Label` |
| | 图片 | `Image` |
| | 图标 | `Icon` |
| | 分隔符 | `Separator` |
| **特殊控件** | 网页浏览器 | `WebBrowser` |
| | 流程图 | `BPMGraph` |
| | 动态控件 | `Dynamic` |
| | 自定义控件 | `Custom` |
| | 嵌入控件 | `Embed` |
| | 甘特图 | `Gantt` |

## 公共属性（yigo-BaseControl-Attr）

所有控件共享的基础属性：

| 属性 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `Key` | string | ✅ | 控件唯一标识 |
| `Caption` | string | ❌ | 显示标题 |
| `CaptionEn` | string | ❌ | 英文标题 |
| `LabelType` | 枚举 | ❌ | 标签类型 |
| `FormulaCaption` | string | ❌ | 动态标题表达式 |
| `Visible` | Boolean/Formula | ❌ | 可见性 |
| `Enable` | Boolean/Formula | ❌ | 可用性 |
| `InitEnable` | Boolean | ❌ | 初始可用状态 |
| `InitVisible` | Boolean | ❌ | 初始可见状态 |
| `CopyNew` | Boolean | ❌ | 新增时是否复制值 |
| `Clearable` | Boolean | ❌ | 是否可清除 |
| `Tip` | string | ❌ | 鼠标提示信息 |
| `FormulaTip` | string | ❌ | 动态提示信息 |
| `OnlyShow` | Boolean | ❌ | 仅显示（移动端） |
| `OneTimeCompute` | Boolean | ❌ | 默认值仅计算一次 |
| `AsQuery` | Boolean | ❌ | 是否叙时簿查询字段 |
| `X`/`Y`/`XSpan`/`YSpan` | int | ❌ | 网格布局坐标定位 |
| `Width`/`Height` | Dimension | ❌ | 尺寸 |
| `HAlign`/`VAlign` | 枚举 | ❌ | 对齐方式 |

## 公共子元素（ControlBaseChildGroup）

大多数控件包含以下公共子元素：

```xml
<控件 Key="..." Caption="...">
    <DataBinding TableKey="表标识" ColumnKey="列标识" />
    <Format>格式化定义</Format>
    <Condition>条件控制定义</Condition>
</控件>
```

### DataBinding（数据绑定）

将控件绑定到 DataObject 的某个 Table.Column：

```xml
<DataBinding TableKey="PurchaseOrder" ColumnKey="PONo"/>

<!-- 带校验规则 -->
<DataBinding TableKey="EPM_Strategy" ColumnKey="Code">
    <CheckRule><![CDATA[IIF(Code=='', '请输入代码', true)]]></CheckRule>
</DataBinding>

<!-- 带默认值公式 -->
<DataBinding DefaultFormulaValue="Macro_MultiLangText('EPM_Strategy','Name')"/>

<!-- 带值改变事件 -->
<DataBinding ColumnKey="PackageUnitID">
    <ValueChanged><![CDATA[com.bokesoft.erp.pm.function.StrategiesFormula.setMinUnitID();]]></ValueChanged>
</DataBinding>
```

### Format（格式化）

控制控件的显示格式。

### Condition（查询条件） ⭐ 报表/查询表单重要

用于报表/查询表单中，将控件值作为查询条件关联到结果表字段：

```xml
<!-- 简单等值条件 -->
<Condition ColumnKey="MaintPlantID" TableKey="{ResultTableKey}" CondSign="=" UseAdvancedQuery="true"/>

<!-- like 模糊查询 -->
<Condition ColumnKey="DocumentNumber" TableKey="{ResultTableKey}" CondSign="like" LoadHistoryInput="true" UseAdvancedQuery="true"/>

<!-- 自定义条件（日期转换等复杂场景） -->
<Condition TableKey="{ResultTableKey}" CondSign="custom" UseAdvancedQuery="true">
    <CustomCondition Condition="Cond_CreateDate &gt; 0" Filter="CAST(CreateTime as DATE) = ${Cond_CreateDate_Para1}">
        <CustomConditionPara Key="Cond_CreateDate_Para1" Formula="Replace(ToString(ConditionPara('Cond_CreateDate')), '-', '')"/>
    </CustomCondition>
    <CustomCondition Condition="1 == 1" Filter=" 1 = 1 "/>
</Condition>
```

| 属性 | 说明 |
|------|------|
| `ColumnKey` | 结果表中对应的列名 |
| `TableKey` | 结果表标识 |
| `CondSign` | 条件符号：`=`, `like`, `custom` |
| `UseAdvancedQuery` | 是否使用高级查询 |
| `LoadHistoryInput` | 是否加载历史输入 |

---

## 常用控件详细说明

### 1. TextEditor（文本编辑器）

```xml
<TextEditor Key="PONo" Caption="订单编号" MaxLength="30" Trim="true" PromptText="请输入编号">
    <DataBinding TableKey="PurchaseOrder" ColumnKey="PONo" />
    <KeyEnter>回车事件公式</KeyEnter>
</TextEditor>
```

| 专有属性 | 说明 |
|----------|------|
| `MaxLength` | 最大长度 |
| `Trim` | 是否去除首尾空格 |
| `PromptText` | 输入提示文字 |
| `EmbedText` | 嵌入文本 |
| `InvalidChars` | 无效字符 |
| `Case` | 大小写转换：`Upper`/`Lower` |
| `PreIcon` | 前置图标 |
| `DisableKeyboard` | 禁用键盘 |

### 2. NumberEditor（数字编辑器）

```xml
<NumberEditor Key="Amount" Caption="金额" Precision="18" Scale="2" 
              ShowZero="false" UseGroupingSeparator="true">
    <DataBinding TableKey="PurchaseOrder" ColumnKey="TotalAmount" />
</NumberEditor>
```

| 专有属性 | 说明 |
|----------|------|
| `Precision` | 数值精度（整数位+小数位） |
| `Scale` | 小数位数 |
| `ZeroString` | 零值显示文本 |
| `UseGroupingSeparator` | 千分位分隔符 |
| `StripTrailingZeros` | 去除尾部零 |
| `ShowZero` | 是否显示零值 |
| `SelectOnFocus` | 获得焦点时全选 |
| `RoundingMode` | 舍入模式 |

### 3. Dict（字典选择控件）

```xml
<Dict Key="SupplierID" Caption="供应商" ItemKey="Supplier" 
      AllowMultiSelection="false" Editable="true" PromptText="选择供应商">
    <DataBinding TableKey="PurchaseOrder" ColumnKey="SupplierID" />
    <ItemFilter Key="filterKey" Type="..." Query="过滤条件" />
</Dict>
```

| 专有属性 | 说明 |
|----------|------|
| `ItemKey` | 字典项标识（关联 DomainDef），意味着有一个数据对象的key与ItemKey一致 |
| `AllowMultiSelection` | 是否允许多选 |
| `Editable` | 是否可编辑 |
| `Independent` | 是否独立 |
| `Root` | 字典根节点 |
| `TextField` | 文本字段 |
| `LoadType` | 加载策略：`Full`/`Lazy` 等 |
| `EditValue` | 是否可编辑值 |
| `QueryMatchType` | 模糊匹配类型 |
| `StateMask` | 状态掩码 |
| `FormulaText` | 表达式文本 |

### 4. ComboBox（下拉框）

```xml
<ComboBox Key="Status" Caption="状态" SourceType="Static" IntegerValue="true">
    <DataBinding TableKey="PurchaseOrder" ColumnKey="Status" />
    <Item Key="0" Caption="草稿" />
    <Item Key="1" Caption="已提交" />
    <Item Key="2" Caption="已审批" />
</ComboBox>
```

| 专有属性 | 说明 |
|----------|------|
| `SourceType` | 来源类型：`Static`/`Domain`/`Formula`/`ParaGroup` |
| `IntegerValue` | 值是否为整数 |
| `Editable` | 是否可编辑 |
| `GroupKey` | 分组标识（`ParaGroup` 时需要） |
| `TextShowType` | 文本显示类型 |
| `Cache` | 是否缓存 |

**校验规则：**
- `SourceType='Formula'` 时必须配置 `FormulaItems` 子元素
- `SourceType='ParaGroup'` 时必须配置 `GroupKey` 属性

### 5. DatePicker（日期选择器）

```xml
<DatePicker Key="PODate" Caption="订单日期" Format="yyyy-MM-dd" EditType="Calendar">
    <DataBinding TableKey="PurchaseOrder" ColumnKey="PODate" />
</DatePicker>
```

| 专有属性 | 说明 |
|----------|------|
| `Format` | 日期格式 |
| `EditType` | 编辑样式：`Calendar`/`Spinner` |

### 6. CheckBox（复选框）

```xml
<CheckBox Key="IsUrgent" Caption="是否紧急">
    <DataBinding TableKey="PurchaseOrder" ColumnKey="IsUrgent" />
</CheckBox>
```

| 专有属性 | 说明 |
|----------|------|
| `CheckedType` | 选中联动类型 |
| `UnCheckedType` | 取消选中联动类型 |
| `IconLocation` | 图标位置 |
| `CheckOnClickNode` | 点击节点时是否选中 |

### 7. Button（按钮）

```xml
<Button Key="btnCalc" Caption="计算" Type="Normal">
    <OnClick><![CDATA[ERPShowModal('TargetForm', GetCallFormula('Macro_ShowEvent', SOID));]]></OnClick>
</Button>
```

| 专有属性 | 说明 |
|----------|------|
| `Icon` | 图标路径 |
| `IconLocation` | 图标位置 |
| `NeedAccessLog` | 需要访问日志 |
| `OnlyIcon` | 仅显示图标 |
| `Type` | 按钮类型 |
| `Activity` | 活动标识 |
| `TCode` | 交易码 |

### 8. Label（标签）

```xml
<Label Key="lblTitle" Caption="采购订单">
    <DataBinding TableKey="PurchaseOrder" ColumnKey="PONo" />
</Label>
```

### 9. TextButton（文本按钮）

```xml
<TextButton Key="vendorSelect" Caption="供应商" UseFormulaModel="false">
    <DataBinding TableKey="PurchaseOrder" ColumnKey="SupplierID" />
    <OnClick>点击事件</OnClick>
    <KeyEnter>回车事件</KeyEnter>
</TextButton>
```

### 10. Image（图片控件）

```xml
<Image Key="productImg" Caption="产品图片" SourceType="DataObject">
    <DataBinding TableKey="PurchaseOrder" ColumnKey="ImageData" />
</Image>
```

### 11. Embed（嵌入控件）

```xml
<Embed Key="embedForm" Caption="嵌入表单" FormKey="TargetFormKey" IncludeDataTable="true">
    <Var Key="varName" />
</Embed>
```

### 12. DropdownButton（下拉按钮）

```xml
<DropdownButton Key="btnMore" Caption="更多操作">
    <DropdownItem Key="item1" Text="操作1">
        <OnClick>操作1事件</OnClick>
    </DropdownItem>
    <DropdownItem Key="item2" Text="操作2">
        <OnClick>操作2事件</OnClick>
    </DropdownItem>
</DropdownButton>
```

---

## 控件放置位置

控件可以放在以下面板内：

- `GridLayoutPanel`（网格布局 — 通过 X/Y 定位，**抬头控件优先使用**）
- `FlexFlowLayoutPanel`（弹性流式布局）
- `FlexGridLayoutPanel`（弹性网格布局）
- `FlowLayoutPanel`（流式布局）
- `BorderLayoutPanel`（边框布局）
- `TabPanel`（分页面板）
- `SplitPanel`（分割面板）

## 与其他 Skill 的配合

- 控件放置在 [面板布局](#panel-layout) 生成的面板内
- 控件的 `DataBinding` 引用 [DataObject 生成](#dataobject-generator) 定义的 Table/Column
- 表格中的单元格也用到控件属性 → 参考 [Grid 表格生成](#grid-generator)
- 报表/查询表单中控件的 `Condition` 子元素 → 参考模板 3/4 in [Form 脚手架](#form-scaffold)
- 按钮/事件中的公式内容 → 参考 [表达式书写](#expression-writer)


---

<a id="grid-generator"></a>
# YIGO Grid 表格控件生成

## 概述

本 Skill 负责生成 YIGO Form XML 中最复杂的组件 —— **Grid 表格控件**。Grid 用于展示和编辑明细数据，包含列定义、行定义、单元格配置、事件和分页等。

> **Grid 内所有事件公式都必须用 `<![CDATA[]]>` 包裹**，包括 `RowClick`, `RowDblClick`, `OnRowDelete`, `CheckRule`, `ValueChanged` 等。

## XSD 参考文件

- 主文件：Grid.xsd（913 行）
- 控件属性：ControlAttributeGroupDefinition.xsd

## Grid 完整结构

```xml
<Grid Key="gridKey" Caption="表格名称" 
      TableKey="绑定的主表Key" 
      SelectionMode="Row" ShowRowHead="true" 
      PageLoadType="UI" PageRowCount="20">
    
    <!-- 条件 -->
    <Condition>...</Condition>
    
    <!-- 列定义集合 -->
    <GridColumnCollection>
        <GridColumn Key="col1" Caption="列标题1" Width="120" Sortable="true" />
        <GridColumn Key="col2" Caption="列标题2" Width="150" ColumnType="Group" />
    </GridColumnCollection>
    
    <!-- 行定义集合 -->
    <GridRowCollection>
        <GridRow Key="row1" TableKey="表标识" RowType="Detail">
            <GridCell Key="cell1" CellType="TextEditor">
                <DataBinding TableKey="表标识" ColumnKey="列标识" />
            </GridCell>
        </GridRow>
    </GridRowCollection>
    
    <!-- 事件（内容用 CDATA 包裹） -->
    <RowClick><![CDATA[行点击事件公式]]></RowClick>
    <RowDblClick><![CDATA[行双击事件公式]]></RowDblClick>
    <BeforeRowInsert><![CDATA[行添加前事件]]></BeforeRowInsert>
    <RowInsert><![CDATA[行添加事件]]></RowInsert>
    <RowDelete><![CDATA[行删除后事件]]></RowDelete>
    <OnRowDelete><![CDATA[行删除事件]]></OnRowDelete>
    <onBatchRowDelete><![CDATA[批量删除事件]]></onBatchRowDelete>
    
    <!-- 追溯集合 -->
    <TraceCollection>
        <Trace Caption="追溯标题" Condition="条件">追溯公式</Trace>
    </TraceCollection>
    
    <!-- 额外操作集合 -->
    <ExtOptCollection>
        <ExtOpt Key="opt1" Caption="操作1" Icon="icon.png">操作公式</ExtOpt>
    </ExtOptCollection>
    
    <!-- 焦点行改变事件 -->
    <FocusRowChanged>焦点行改变公式</FocusRowChanged>
    
    <!-- 数据过滤 -->
    <GridFilter Op="And">
        <FilterValue FieldKey="字段" CondSign="=" ParaValue="值" />
    </GridFilter>
</Grid>
```

## Grid 属性

| 属性 | 类型 | 说明 |
|------|------|------|
| `Key` | string | ✅ 表格唯一标识 |
| `Caption` | string | 表格标题 |
| `SelectionMode` | `Cell`/`Row` | 选择模式（默认范围选择） |
| `ShowRowHead` | Boolean | 是否显示行头序号列 |
| `SerialSeq` | Boolean | 序列号是否连续 |
| `DefaultFitWidth` | Boolean | 是否默认最佳列宽 |
| `Option` | string | 需要的操作定义 |
| `DisabledOption` | string | 不需要的操作定义 |
| `GridDefaultSortField` | string | 默认排序公式 |
| `EndEditByNav` | Boolean | 方向键结束编辑 |
| `NewEmptyRow` | Boolean/Formula | 编辑状态下是否新增空行 |
| `OneTimeCompute` | Boolean | 一次性计算 |
| `ShowTotalRowCount` | Boolean | 显示总行数 |
| `AddDataRow` | Boolean | 插行时是否同步插入数据行 |
| `Zoom` | Boolean | 是否缩放 |
| `Sortable` | Boolean | 列是否可拖动排序 |
| `Custom` | Boolean | 是否有定制数据 |
| `Locate` | Boolean | 是否可定位行 |

### 分页属性（yigo-Grid-Page）

| 属性 | 类型 | 说明 |
|------|------|------|
| `PageLoadType` | `UI`/`DB` | 分页方式（前端分页/后端分页） |
| `PageRowCount` | int | 每页行数 |
| `PageIndicatorCount` | int | 显示最大页码 |
| `SerialRowNum` | Boolean | 序列号行号 |
| `RowRange` | string | 可选每页行数列表 |

## GridColumn（列定义）

```xml
<GridColumn Key="colKey" Caption="列标题" Width="120" 
            ColumnType="Detail" Sortable="true" Freeze="false">
    <!-- 列拓展（可选） -->
    <ColumnExpand ExpandType="..." ExpandSourceType="..." />
    <!-- 嵌套列（可选） -->
    <GridColumnCollection>
        <GridColumn Key="subCol1" Caption="子列1" />
    </GridColumnCollection>
    <!-- 选中事件（复选框列用） -->
    <OnSelect>选中事件公式</OnSelect>
</GridColumn>
```

| 属性 | 类型 | 说明 |
|------|------|------|
| `Key` | string | ✅ 列标识（同一集合内唯一） |
| `Caption` | string | 列标题 |
| `Width` | string | 列宽 |
| `ColumnType` | `Detail`/`Group`/`Total` | 列类型（默认 Fix） |
| `Sortable` | Boolean | 是否可排序 |
| `Freeze` | Boolean | 是否冻结 |
| `Fixed` | `left`/`right` | 冻结位置 |
| `Visible` | Boolean/Formula | 可见性 |
| `Enable` | Boolean/Formula | 可用性 |
| `FormulaCaption` | Formula | 动态标题 |
| `LabelType` | 枚举 | 标题标签类型 |
| `SumFormula` | Formula | 汇总公式 |
| `GroupSumFormula` | Formula | 分组汇总公式 |
| `Image` | string | 图片 |

### ColumnExpand（列拓展）

```xml
<ColumnExpand ExpandType="Horizontal" ExpandSourceType="Formula" 
              TableKey="表标识" ColumnKey="列标识" ItemKey="字典标识" 
              ExpandDependency="依赖列">
    拓展公式内容
</ColumnExpand>
```

## GridRow（行定义）

```xml
<GridRow Key="rowKey" TableKey="表标识" RowType="Detail" RowHeight="32">
    <GridCell Key="cellKey" CellType="TextEditor">
        <DataBinding TableKey="表" ColumnKey="列" />
    </GridCell>
    <!-- 行拓展（可选） -->
    <RowExpand ExpandType="..." CellKey="..." />
    <!-- 行树形（可选） -->
    <RowTree CellKey="treeCell" TreeType="..." Expand="true" ExpandLevel="2" />
</GridRow>
```

| 属性 | 类型 | 说明 |
|------|------|------|
| `Key` | string | ✅ 行标识 |
| `TableKey` | string(50) | 绑定的表标识 |
| `RowType` | `Fix`/`Group`/`Total`/`TreeRow` | 行类型（默认 Detail） |
| `RowHeight` | int | 行高 |
| `Visible` | Boolean/Formula | 可见性（非明细行） |
| `GroupKey` | string | 分组标识（分组行用） |
| `BackColor` | Color | 背景色 |

## GridCell（单元格） ⭐ 核心配置

GridCell 是 Grid 最核心的配置，它定义了每个单元格的控件类型和数据绑定。

```xml
<GridCell Key="cellKey" CellType="控件类型" Caption="显示名"
          Visible="true" Enable="true">
    <DataBinding TableKey="表标识" ColumnKey="列标识" />
    <ItemFilter Key="过滤标识" Type="..." Query="条件" />
    <Item Key="选项Key" Caption="选项名" />
    <OnClick>点击事件</OnClick>
    <DblClick>双击事件</DblClick>
    <CellFormat>单元格格式</CellFormat>
</GridCell>
```

### CellType 对应的控件类型

GridCell 的 `CellType` 属性决定单元格使用哪种控件，取值参考 `yigo-ControlType-Biz`：

| CellType 值 | 对应控件 | 常用附加属性 |
|-------------|----------|-------------|
| `TextEditor` | 文本输入 | `MaxLength`, `Trim`, `Case` |
| `NumberEditor` | 数字输入 | `Precision`, `Scale`, `ShowZero` |
| `ComboBox` | 下拉框 | `SourceType`, `GroupKey`, `IntegerValue` |
| `CheckBox` | 复选框 | `CheckedType`, `UnCheckedType` |
| `CheckListBox` | 复选列表 | `SourceType` |
| `RadioButton` | 单选按钮 | `GroupKey`, `Value` |
| `DatePicker` | 日期选择 | `Format`, `EditType` |
| `UTCDatePicker` | UTC 日期 | `Format` |
| `MonthPicker` | 月份选择 | `Format` |
| `TimePicker` | 时间选择 | `Format` |
| `Dict` | 字典选择 | `ItemKey`, `AllowMultiSelection`, `LoadType` |
| `DynamicDict` | 动态字典 | `ItemKey`, `RefKey`, `IsDynamic` |
| `Label` | 标签显示 | — |
| `Button` | 按钮 | `NeedAccessLog`, `OnlyIcon` |
| `TextButton` | 文本按钮 | `UseFormulaModel` |
| `Image` | 图片 | `ImageScaleType` |
| `HyperLink` | 超链接 | — |
| `Icon` | 图标 | `Icon`, `ImageScaleType` |
| `RichEditor` | 富文本 | — |
| `Custom` | 自定义 | — |
| `Dynamic` | 动态控件 | — |

> **注意**：GridCell 上会附带对应控件类型的全部属性组。例如 `CellType="Dict"` 时，GridCell 可使用 Dict 控件的所有属性（如 `ItemKey`, `AllowMultiSelection` 等）。

### 单元格其他重要属性

| 属性 | 说明 |
|------|------|
| `IsSelect` | 是否选择字段（复选框列） |
| `SingleSelect` | 选择是否单选 |
| `CopyNew` | 是否可复制新增 |
| `AsQuery` | 是否叙时簿查询字段 |
| `Tip` | 提示信息 |
| `CellGroupType` | 分组类型 |
| `IsMerged` | 是否在合并区域 |
| `Merge` | 是否合并 |

## RowTree（行树形配置）

```xml
<RowTree CellKey="treeCell" TreeType="Standard" Type="selfReferencing"
         Expand="true" ExpandLevel="2" 
         Foreign="外键字段" Parent="父节点字段"
         LoadMethod="Full" Image="tree.png" />
```

## GridFilter（数据过滤）

```xml
<GridFilter Op="And">
    <FilterValue FieldKey="Status" CondSign="=" ParaValue="1" DataType="Int" Type="Const" />
    <FilterValue FieldKey="DeptID" CondSign="=" Type="Field" RefValue="当前部门公式" />
</GridFilter>
```

---

## 使用示例

### 示例 1：标准明细表格（采购订单明细）

```xml
<Grid Key="gridDtl" Caption="订单明细" ShowRowHead="true" NewEmptyRow="true">
    <GridColumnCollection>
        <GridColumn Key="colLineNo" Caption="行号" Width="60" />
        <GridColumn Key="colMaterial" Caption="物料" Width="150" />
        <GridColumn Key="colQty" Caption="数量" Width="100" />
        <GridColumn Key="colPrice" Caption="单价" Width="120" />
        <GridColumn Key="colAmount" Caption="金额" Width="120" />
        <GridColumn Key="colRemark" Caption="备注" Width="200" />
    </GridColumnCollection>
    <GridRowCollection>
        <GridRow Key="dtlRow" TableKey="PurchaseOrderDtl">
            <GridCell Key="LineNo" CellType="NumberEditor" Precision="10" Scale="0" Enable="false">
                <DataBinding TableKey="PurchaseOrderDtl" ColumnKey="LineNo" />
            </GridCell>
            <GridCell Key="MaterialID" CellType="Dict" ItemKey="Material">
                <DataBinding TableKey="PurchaseOrderDtl" ColumnKey="MaterialID" />
            </GridCell>
            <GridCell Key="Qty" CellType="NumberEditor" Precision="18" Scale="4">
                <DataBinding TableKey="PurchaseOrderDtl" ColumnKey="Qty" />
            </GridCell>
            <GridCell Key="Price" CellType="NumberEditor" Precision="18" Scale="4">
                <DataBinding TableKey="PurchaseOrderDtl" ColumnKey="Price" />
            </GridCell>
            <GridCell Key="Amount" CellType="NumberEditor" Precision="18" Scale="2" Enable="false">
                <DataBinding TableKey="PurchaseOrderDtl" ColumnKey="Amount" />
            </GridCell>
            <GridCell Key="Remark" CellType="TextEditor" MaxLength="200">
                <DataBinding TableKey="PurchaseOrderDtl" ColumnKey="Remark" />
            </GridCell>
        </GridRow>
    </GridRowCollection>
</Grid>
```

### 示例 2：叙时簿列表表格（带分页）

```xml
<Grid Key="gridList" Caption="采购订单列表" 
      SelectionMode="Row" PageLoadType="DB" ShowRowHead="true">
    <GridColumnCollection>
        <GridColumn Key="colPONo" Caption="订单编号" Width="120" Sortable="true" />
        <GridColumn Key="colDate" Caption="订单日期" Width="100" Sortable="true" />
        <GridColumn Key="colSupplier" Caption="供应商" Width="150" />
        <GridColumn Key="colAmount" Caption="总金额" Width="120" />
        <GridColumn Key="colStatus" Caption="状态" Width="80" />
    </GridColumnCollection>
    <GridRowCollection>
        <GridRow Key="listRow" TableKey="PurchaseOrder">
            <GridCell Key="PONo" CellType="TextEditor" Enable="false">
                <DataBinding TableKey="PurchaseOrder" ColumnKey="PONo" />
            </GridCell>
            <GridCell Key="PODate" CellType="DatePicker" Format="yyyy-MM-dd" Enable="false">
                <DataBinding TableKey="PurchaseOrder" ColumnKey="PODate" />
            </GridCell>
            <GridCell Key="SupplierID" CellType="Dict" ItemKey="Supplier" Enable="false">
                <DataBinding TableKey="PurchaseOrder" ColumnKey="SupplierID" />
            </GridCell>
            <GridCell Key="TotalAmount" CellType="NumberEditor" Precision="18" Scale="2" Enable="false">
                <DataBinding TableKey="PurchaseOrder" ColumnKey="TotalAmount" />
            </GridCell>
            <GridCell Key="Status" CellType="ComboBox" SourceType="Static" Enable="false">
                <DataBinding TableKey="PurchaseOrder" ColumnKey="Status" />
                <Item Key="0" Caption="草稿" />
                <Item Key="1" Caption="已提交" />
            </GridCell>
        </GridRow>
    </GridRowCollection>
    <RowDblClick><![CDATA[Open("PurchaseOrder")]]></RowDblClick>
</Grid>
```

## 与其他 Skill 的配合

- Grid 放在 [面板布局](#panel-layout) 生成的面板内（如 Block/FlexFlowLayoutPanel）
- GridCell 的 `CellType` 与 [UI 控件生成](#control-generator) 中的控件类型对应，属性规格一致
- GridCell 的 `DataBinding` 引用 [DataObject 生成](#dataobject-generator) 定义的 Table/Column
- Grid 的事件公式内容 → 参考 [表达式书写](#expression-writer)


---

<!-- =========================================================== -->
# 第七部分：逻辑层（Operations → Expression → Java 二开）
<!-- =========================================================== -->

<a id="operation-script"></a>
# YIGO 操作与脚本生成

## 概述

本 Skill 负责生成 YIGO Form XML 中的**操作定义（OperationCollection）**、**脚本集合（ScriptCollection）**和**宏公式集合（MacroCollection）**。这三个组件定义了表单的业务逻辑行为。

> **所有 `Action`、`ExceptionHandler`、`Macro` 内容、`OnLoad`/`OnClose` 等公式体都必须用 `<![CDATA[]]>` 包裹**。

## XSD 参考文件

- 操作集合：OperationCollection.xsd
- 脚本定义：BaseScriptDefinition.xsd
- 宏公式：MacroCollection.xsd
- 公共定义：CommonDefDefine.xsd
- 操作属性：AttributeGroupDefinition.xsd → `yigo-Operation-Attr`

---

## 1. OperationCollection（操作定义集合）

### 结构

```xml
<OperationCollection>
    <!-- 直接的操作 -->
    <Operation Key="optKey" Caption="操作名称">
        <Action><![CDATA[操作执行的公式内容]]></Action>
        <ExceptionHandler><![CDATA[异常处理公式]]></ExceptionHandler>
        <!-- 子操作（可嵌套） -->
        <Operation Key="subOpt" Caption="子操作">
            <Action><![CDATA[子操作公式]]></Action>
        </Operation>
    </Operation>
    
    <!-- 分组的操作集合 -->
    <OperationCollection Key="groupKey" Caption="操作分组">
        <Operation Key="opt1" Caption="操作1">
            <Action><![CDATA[公式]]></Action>
        </Operation>
        <Operation Key="opt2" Caption="操作2">
            <Action><![CDATA[公式]]></Action>
        </Operation>
    </OperationCollection>
</OperationCollection>
```

### 层级说明

- **顶层 OperationCollection**：Form 直接的子元素，**无属性**
- **嵌套 OperationCollection**：有 `yigo-Operation-Attr` 属性，用于分组
- **Operation**：具体的操作项，可嵌套子 Operation

### Operation 属性（yigo-Operation-Attr）

| 属性 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `Key` | string | ✅ | 操作唯一标识（同级唯一） |
| `Caption` | string | ❌ | 操作显示名称 |
| `CaptionEn` | string | ❌ | 英文名称 |
| `Visible` | Boolean/Formula | ❌ | 是否可见 |
| `Enable` | Boolean/Formula | ❌ | 是否可用 |
| `VisibleDependency` | string | ❌ | 可见性依赖项 |
| `EnableDependency` | string | ❌ | 可用性依赖项 |
| `RefKey` | string | ❌ | 引用标识（引用公共操作） |
| `Icon` | string | ❌ | 图标 |
| `ShortCuts` | string | ❌ | 快捷键（如 `Ctrl+S`） |
| `SelfDisable` | Boolean | ❌ | 是否自动禁用（防连点） |
| `NeedAccessLog` | Boolean | ❌ | 是否需要访问日志 |
| `CssClass` | string | ❌ | CSS 类名 |
| `IconCode` | string | ❌ | 图标编码 |
| `TCode` | string | ❌ | 交易码 |
| `Activity` | string | ❌ | 活动标识 |
| `Tag` | string | ❌ | 标签 |
| `ExpandSource` | string | ❌ | 展开来源 |
| `Expand` | Boolean | ❌ | 是否展开 |
| `IsTransfer` | Boolean | ❌ | 是否转换 |

### Operation 子元素

| 子元素 | 说明 |
|--------|------|
| `Action` | 操作执行的公式/脚本内容 |
| `ExceptionHandler` | 异常处理公式 |
| `Operation` | 嵌套的子操作 |

---

## 2. ScriptCollection（脚本集合）

```xml
<ScriptCollection>
    <!-- 脚本内容，Type 可选 Formula/Java 等（默认 Formula，可省略） -->
</ScriptCollection>
```

### BaseScript 类型

| 属性 | 说明 |
|------|------|
| `Type` | 脚本类型（`Formula` 为默认值，ERP 中通常都是 Formula） |

### 在 Form 中的使用位置

Form 中有多个使用脚本类型的地方：
- `ScriptCollection` — 脚本集合
- `OnLoad` — 表单加载事件
- `OnClose` — 表单关闭事件
- `OnPostShow` — 表单显示后事件
- Grid 中的 `RowClick`/`RowDblClick`/`RowInsert`/`RowDelete` 等
- Button 的 `OnClick`
- 控件的 `KeyEnter`

这些元素都使用 `yigo-BaseScript2` 类型（mixed content，脚本内容直接写在元素体内）：

```xml
<OnLoad><![CDATA[初始化公式内容]]></OnLoad>
<OnClick><![CDATA[按钮点击公式]]></OnClick>
<RowDblClick><![CDATA[SetFormState("Edit")]]></RowDblClick>
```

---

## 3. MacroCollection（宏公式集合）

```xml
<MacroCollection>
    <Macro Key="宏标识" Args="参数列表">
        <![CDATA[宏公式内容]]>
    </Macro>
</MacroCollection>
```

### Macro 属性

| 属性 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `Key` | string | ✅ | 宏唯一标识（集合内唯一） |
| `Args` | string | ❌ | 参数列表 |

**内容**：宏的公式体，直接作为元素的文本内容。

---

## 使用示例

### 示例 1：标准实体表单操作集合（增删改查审核）

```xml
<OperationCollection>
    <Operation Key="New" Caption="新增" ShortCuts="Ctrl+N">
        <Action><![CDATA[New()]]></Action>
    </Operation>
    <Operation Key="Edit" Caption="编辑">
        <Action><![CDATA[Edit()]]></Action>
    </Operation>
    <Operation Key="Save" Caption="保存" ShortCuts="Ctrl+S" SelfDisable="true">
        <Action><![CDATA[Save()]]></Action>
        <ExceptionHandler><![CDATA[ShowMessage(GetLastError())]]></ExceptionHandler>
    </Operation>
    <Operation Key="Delete" Caption="删除">
        <Action><![CDATA[if(Confirm("确定要删除吗？")) { Delete() }]]></Action>
    </Operation>
    <Operation Key="Cancel" Caption="取消">
        <Action><![CDATA[Cancel()]]></Action>
    </Operation>
    <!-- 审核分组 -->
    <OperationCollection Key="ApproveGroup" Caption="审核">
        <Operation Key="Submit" Caption="提交" Enable="GetFieldValue(&quot;Status&quot;)==0">
            <Action><![CDATA[Submit()]]></Action>
        </Operation>
        <Operation Key="Approve" Caption="审批" Enable="GetFieldValue(&quot;Status&quot;)==1">
            <Action><![CDATA[Approve()]]></Action>
        </Operation>
    </OperationCollection>
    <Operation Key="Close" Caption="关闭">
        <Action><![CDATA[CloseForm()]]></Action>
    </Operation>
</OperationCollection>
```

### 示例 2：View 表单操作集合（叙时簿）

```xml
<OperationCollection>
    <Operation Key="Query" Caption="查询" Icon="query.png">
        <Action>Query()</Action>
    </Operation>
    <Operation Key="New" Caption="新增" Icon="new.png">
        <Action>OpenForm("PurchaseOrder", "New")</Action>
    </Operation>
    <Operation Key="Edit" Caption="编辑" Icon="edit.png">
        <Action>OpenForm("PurchaseOrder", "Edit")</Action>
    </Operation>
    <Operation Key="Delete" Caption="删除" Icon="delete.png">
        <Action>DeleteSelected()</Action>
    </Operation>
    <Operation Key="Export" Caption="导出" Icon="export.png">
        <Action>Export("Excel")</Action>
    </Operation>
    <Operation Key="Close" Caption="关闭" Icon="close.png">
        <Action>CloseForm()</Action>
    </Operation>
</OperationCollection>
```

### 示例 3：宏公式集合

```xml
<MacroCollection>
    <Macro Key="CalcAmount" Args="qty,price">
        <![CDATA[SetFieldValue("Amount", qty * price)]]>
    </Macro>
    <Macro Key="ValidateBeforeSave">
        <![CDATA[IIF(IsEmpty(GetFieldValue("PONo")), ShowMessage("订单编号不能为空"), true)]]>
    </Macro>
</MacroCollection>
```

### 示例 4：表单事件

```xml
<Form Key="PurchaseOrder" Caption="采购订单" FormType="Entity">
    <!-- ... -->
    <OnLoad><![CDATA[IIF(GetFormState()=="New", SetFieldValue("PODate", Today()), '')]]></OnLoad>
    <OnClose><![CDATA[IIF(IsModified(), IIF(Confirm("数据已修改，是否保存？"), Save(), ''), '')]]></OnClose>
</Form>
```

---

## CommonDef 中的操作集合

CommonDef 定义的是**公共操作**，可被多个表单引用（通过 `RefKey`）：

```xml
<CommonDef>
    <OperationCollection>
        <Operation Key="CommonSave" Caption="保存">
            <Action>Save()</Action>
        </Operation>
    </OperationCollection>
    <StatusCollection>...</StatusCollection>
    <ScriptCollection>...</ScriptCollection>
    <MacroCollection>...</MacroCollection>
</CommonDef>
```

在 Form 中引用公共操作：

```xml
<Operation Key="Save" RefKey="CommonSave" />
```

## 与其他 Skill 的配合

- OperationCollection 是 [Form 脚手架](#form-scaffold) 中 Form 的子元素
- Operation 的 `Action` 公式可能引用 [DataObject 生成](#dataobject-generator) 定义的字段
- Button 控件的 `OnClick` 事件内容格式与此相同 → 参考 [UI 控件生成](#control-generator)
- 公式语法细节 → 参考 [表达式书写](#expression-writer)


---

<a id="expression-writer"></a>
# YIGO 表达式书写

## 概述

YIGO 表达式（Formula）是嵌入在 Form XML 中的脚本语言，用于控制界面行为、计算字段值、校验数据等。表达式出现在 `Action`、`OnLoad`、`OnClick`、`CheckRule`、`Macro`、`DefaultValue`（公式类）、`Visible`/`Enable`（公式类）、`ValueChanged` 等位置。

> **核心规则**：在 XML 中，所有表达式体必须用 `<![CDATA[...]]>` 包裹。

## BNF 语法参考

完整 BNF 定义：[BNF 语法定义](#bnf-grammar)

## XML 实体转义 ⚠️

在 XML **属性值**中（如 `Visible`、`Enable`），不能使用 CDATA，因此必须对特殊字符进行

### IIF 三元表达式

```
IIF(条件, 真值, 假值)
```

嵌套 IIF：

```
IIF(条件1, 值1, IIF(条件2, 值2, 值3))
```

IIFS（多条件分支）：

```
IIFS(条件1, 值1, 条件2, 值2, ...)
```


### Return

```
return 表达式;
```

## 函数调用

### 内置函数

直接调用：`FunctionName(参数1, 参数2, ...)`

常见内置函数：

| 函数 | 说明 |
|------|------|
| `ReadOnly()` | 当前是否只读状态 |
| `Length(value)` | 转整数 |
| `SetPara(paraKey, paraValue)` | 设置参数 |
| `GetPara(paraKey)` | 获取参数 |
| `MessageFacade(code, text)` | 弹出消息 |

### Macro 宏调用

在 Form XML 的 `MacroCollection` 中定义的宏：

```
Macro_宏名称(参数1, 参数2)
```

- 宏名以 `Macro_` 开头
- 在 `<MacroCollection>` 中定义，Key 为 `宏名称`（不含 `Macro_` 前缀）
- Form 中同名宏会覆盖 `CommonDef.xml` 中的同名宏

### Java 静态方法调用

通过完整类路径调用 Java 二开方法：

```
com.bokesoft.erp.pm.function.StrategiesFormula.isExistStrategiesPackage()
com.bokesoft.erp.pm.function.StrategiesFormula.checkCycleLength(SchedulingIndicator, PackageUnitID, CycleLength, IsCycleSet)
```

- 使用完整包路径 `com.xxx.yyy.ClassName.methodName(...)`
- Java 方法必须继承 `EntityContextAction`（参考 [Java 二次开发](#java-customization) skill）
- 参数可以是字段标识、常量或表达式

### parent / container 上下文

```
parent.方法名()       // 调用父表单的方法
container.方法名()    // 调用容器的方法
Parent.方法名()       // 大小写不敏感
```

典型用途：条件表单调用父表单加载数据

```
DealCondition(true);parent.Macro_LoadObject()
```

## ConfirmMsg 确认对话框

```
ConfirmMsg(消息代码, 消息文本, 消息参数, 样式, 回调对象)
```

| 参数 | 必须 | 说明 |
|------|------|------|
| 消息代码 | ✅ | 消息 Key |
| 消息文本 | ✅ | 显示文本 |
| 消息参数 | ❌ | `{{参数1},{参数2}}` 格式 |
| 样式 | ❌ | `OK`、`YES_NO`、`YES_NO_CANCEL` |
| 回调对象 | ❌ | `{yes: {执行语句}, no: {执行语句}}` |

### 示例

```
ConfirmMsg('MSG001', '确定要删除吗？', {}, 'YES_NO', {yes: {Delete()}, no: {}})
```

## 多语句书写

多条语句用 `;` 分隔：

```xml
<Action><![CDATA[
var x = GetFieldValue('Status');
if (x == 0) {
    SetFieldValue('Status', 1);
    Save();
};
Refresh()
]]></Action>
```

## 常见表达式模式

### 1. 条件可见/可用（属性中）

```xml
<TextEditor Visible="!ToInt(IsCycleSet)" Enable="!ReadOnly()&amp;&amp;Enable&lt;0"/>
```

### 2. 校验规则（CDATA 中）

```xml
<CheckRule><![CDATA[
IIFS(
    com.bokesoft.erp.documentNumber.DocumentNumber.docNumberFieldEnable('Code')&&Code=='',
    '采用外部给号编码规则，请输入代码',
    true,
    true
)
]]></CheckRule>
```

### 3. 默认值公式

```xml
<DataBinding DefaultFormulaValue="Macro_MultiLangText('EPM_Strategy','Name')"/>
```

### 4. 调用 Java 方法做字典过滤

```xml
<ItemFilter ItemKey="Unit">
    <Filter Key="Filter" Impl="com.bokesoft.yes.erp.condition.Filter" Type="Custom">
        <FilterValue Index="1" ParaValue="IIF(SchedulingIndicator&gt;=0, ' soid in ('&amp;com.bokesoft.erp.pm.function.StrategiesFormula.getStrategyUnitIDbyIndicator(SchedulingIndicator)&amp;' )', '1=2')"/>
    </Filter>
</ItemFilter>
```

## 与其他 Skill 的配合

| 配合 Skill | 使用位置 |
|------------|----------|
| [操作与脚本](#operation-script) | `Action`、`ExceptionHandler`、`Macro` 的内容 |
| [UI 控件生成](#control-generator) | 控件的 `Visible`、`Enable`、`CheckRule`、`ValueChanged` 等 |
| [Form 脚手架](#form-scaffold) | `OnLoad`、`OnClose`、`OnPostShow` 事件 |
| [Java 二次开发](#java-customization) | Java 静态方法调用的实现端 |


---

<a id="java-customization"></a>
# YIGO Java 二次开发（二开）

## 概述

YIGO ERP 的二次开发（二开）是在 XML 表单配置的基础上，通过 Java 代码扩展业务逻辑。二开类在 XML 表达式中通过 `com.包路径.类名.方法名(参数)` 的方式调用。

## 参考文件

- 架构文档：[YIGO 平台架构](#architecture)
- 开发流程：[YIGO ERP 工作流](#workflow)

## 核心类继承关系

```
EntityContextAction          ← 二开类继承此基类
  ├── _context               → RichDocumentContext（上下文）
  │    ├── getRichDocument()  → RichDocument（当前表单数据）
  │    ├── getResultSet(sql)  → DataTable（执行 SQL 查询）
  │    └── getEnv()           → 环境信息（语言、用户等）
  ├── getDocument()           → RichDocument（快捷方法）
  └── getResultSet(sql)       → DataTable（快捷方法）

AbstractBillEntity           ← BillEntity 自动生成类继承此基类
  ├── parseEntity(_context)  → 从上下文解析表单实体
  ├── load(_context, oid)    → 从数据库加载实体
  ├── get字段名()             → 获取字段值
  ├── set字段名(value)        → 设置字段值
  └── 明细表名s()             → 获取明细表实体列表

AbstractTableEntity          ← TableEntity 自动生成类继承此基类
  ├── get字段名()             → 获取列值
  ├── set字段名(value)        → 设置列值
  └── valueByFieldKey(key, value) → 按 FieldKey 操作
```

## 二开类编写规范

### 基本结构

```java
package com.bokesoft.erp.模块.function;

import com.bokesoft.erp.entity.util.EntityContextAction;
import com.bokesoft.yes.mid.cmd.richdocument.strut.RichDocumentContext;

public class XxxFormula extends EntityContextAction {

    public XxxFormula(RichDocumentContext _context) {
        super(_context);
    }

    // 二开方法
    public 返回值 方法名(参数...) throws Throwable {
        // 业务逻辑
    }
}
```

### 关键要求

1. **必须继承** `EntityContextAction`
2. **构造方法**必须接受 `RichDocumentContext` 参数并调用 `super(_context)`
3. 方法声明 `throws Throwable`
4. 在 XML 表达式中通过 `com.bokesoft.erp.模块.function.类名.方法名(参数)` 调用

## BillEntity（表单实体）

BillEntity 是由 YigoCAD 工具自动生成的 Java 类，封装了整个 Form 表单的数据操作。

### 主要能力

| 方法 | 说明 |
|------|------|
| `parseEntity(_context)` | 从当前上下文解析表单实体 |
| `parseDocument(doc)` | 从 RichDocument 解析表单实体 |
| `load(_context, oid)` | 从数据库按 OID 加载实体 |
| `get字段名()` | 获取表头字段值（类型安全） |
| `set字段名(value)` | 设置表头字段值（支持链式调用） |
| `明细表名s()` | 获取明细表实体列表 |
| `明细表名(oid)` | 按主键获取单条明细 |
| `明细表名s(filterKey, filterValue)` | 按字段过滤明细 |
| `new明细表名()` | 新增明细行 |
| `delete明细表名(dtl)` | 删除明细行 |

### 使用示例

```java
// 从上下文解析表单
PM_Strategy strategies = PM_Strategy.parseEntity(_context);

// 读取表头字段
Long unitID = strategies.getStrategyUnitID();
String code = strategies.getCode();

// 设置字段值（链式调用）
strategies.setStrategyUnitID(100L).setCallHorizon(30);

// 遍历明细表
for (EPM_StrategyDtl dtl : strategies.epm_strategyDtls()) {
    Long packageUnitID = dtl.getPackageUnitID();
    int cycleLength = dtl.getCycleLength();
}

// 新增明细行
EPM_StrategyDtl newDtl = strategies.newEPM_StrategyDtl();
newDtl.setPackageNo(1).setCycleNotes("新周期");

// 从数据库加载另一个表单
PM_Strategy other = PM_Strategy.load(_context, targetOID);
```

### 字段常量

BillEntity 中为每个控件（Field）生成了 `public static final String` 常量，格式为：
- 表头字段：`字段Key`（如 `PM_Strategy.SchedulingIndicator`）
- 明细字段：`Dtl_字段Key`（如 `PM_Strategy.Dtl_CycleLength`）
- 操作：`Opt_操作Key`（如 `PM_Strategy.Opt_DicNew`）

## TableEntity（表实体）

TableEntity 封装了 DataObject 中某张表的数据，使用 ColumnKey 访问。

### 与 BillEntity 的区别

| | BillEntity | TableEntity |
|---|-----------|-------------|
| 数据范围 | 整个表单 | 单张表 |
| 访问方式 | FieldKey | ColumnKey |
| get/set | `value_String(Key)` → `getString(Key)` | `value_String(ColumnKey)` |
| 加载 | `parseEntity(_context)` | 从 BillEntity 获取 |

### 使用示例

```java
// 获取 TableEntity
EPM_Strategy tableEntity = strategies.epm_strategy();

// 按 ColumnKey 访问
String code = tableEntity.getCode();
Long clientID = tableEntity.getClientID();

// 动态字段访问
Object value = tableEntity.valueByFieldKey("DuePack1");
tableEntity.valueByFieldKey("DuePack1", "新值");
```

## Loader（数据加载器）

BillEntity/TableEntity 提供 `loader(_context)` 链式加载器：

```java
// 链式条件查询
List<BK_Unit> unitList = BK_Unit.loader(_context)
    .UnitSystemID(systemID)
    .loadList();

// 单条查询
BK_Unit unit = BK_Unit.loader(_context)
    .Code("d")
    .loadFirst();

// 按 OID 加载
BK_Unit unit = BK_Unit.load(_context, unitID);
```

## SqlString（SQL 查询）

当需要执行自定义 SQL 时，使用 `SqlString` 防注入拼接：

```java
SqlString sql = new SqlString()
    .append("select * from ", EPP_Routing_MaintenancePack.EPP_Routing_MaintenancePack,
            " where ", EPP_Routing_MaintenancePack.IsRelation, "=")
    .appendPara(1)
    .append(" and PackageShortText = ")
    .appendPara(billDtlID);

DataTable rst = getResultSet(sql);

// 遍历结果
if (rst != null && rst.size() > 0) {
    for (int rowIndex = 0; rowIndex < rst.size(); rowIndex++) {
        Long soid = rst.getLong(rowIndex, "SOID");
        String name = rst.getString(rowIndex, "Name");
    }
}
```

### SqlString 方法

| 方法 | 说明 |
|------|------|
| `append(str...)` | 拼接 SQL 片段 |
| `appendPara(value)` | 拼接参数化值（防注入） |
| `SqlStringUtil.genMultiParameters(str)` | 生成 IN 子句参数 |

## MessageFacade（消息提示）

```java
// 获取消息文本
String msg = MessageFacade.getMsgContent(MessageConstant.MSG_CODE);

// 抛出异常消息（中断操作）
MessageFacade.throwException(MessageConstant.MSG_CODE, param1, param2);
```

## RichDocumentContext 上下文

```java
// 获取当前表单文档
RichDocument doc = _context.getRichDocument();
// 或使用 EntityContextAction 的快捷方法
RichDocument doc = getDocument();

// 执行 SQL
DataTable rst = _context.getResultSet(sqlString);
// 或快捷方法
DataTable rst = getResultSet(sql);

// 获取 Paras 自定义参数
Object para = _context.getParas().get("key");
_context.getParas().put("key", value);

// 获取环境信息
String locale = _context.getEnv().getLocale();
```

## RichDocument（表单数据）

```java
RichDocument doc = getDocument();

// 读写表头字段
Object value = doc.getHeadFieldValue("FieldKey");
doc.setHeadFieldValue("FieldKey", newValue);

// 获取明细表数据
DataTable dtlTable = doc.getDataTable("TableKey");

// 新增明细行
int rowIndex = doc.appendDetail("TableKey");

// 删除明细行
doc.deleteDetail("TableKey", oid);
```

## 在 XML 中调用二开方法

```xml
<!-- 在控件属性中调用（返回布尔值控制 Enable） -->
<Dict Enable="IIF(ToBool(com.bokesoft.erp.pm.function.StrategiesFormula.isExistStrategiesPackage()),false,!ReadOnly())"/>

<!-- 在 CheckRule 中调用（返回错误消息或空串） -->
<CheckRule><![CDATA[
var msg = com.bokesoft.erp.pm.function.StrategiesFormula.checkCycleLength(SchedulingIndicator, PackageUnitID, CycleLength, IsCycleSet);
IIF(msg!='', msg, true)
]]></CheckRule>

<!-- 在 ItemFilter 中调用（返回 SqlString） -->
<FilterValue Index="1" ParaValue="IIF(SchedulingIndicator&gt;=0, ' soid in ('&amp;com.bokesoft.erp.pm.function.StrategiesFormula.getStrategyUnitIDbyIndicator(SchedulingIndicator)&amp;' )', '1=2')"/>
```

## 与其他 Skill 的配合

| 配合 Skill | 关系 |
|------------|------|
| [表达式书写](#expression-writer) | 表达式语法参考，二开方法在表达式中通过完整类路径调用 |
| [DataObject 生成](#dataobject-generator) | DataObject 的 Table/Column 定义决定了 BillEntity/TableEntity 的字段 |
| [操作与脚本](#operation-script) | 操作的 Action 中调用二开方法 |
| [UI 控件生成](#control-generator) | 控件的 CheckRule/ValueChanged/Enable 中调用二开方法 |


---

<!-- =========================================================== -->
# 第八部分：数据初始化与权限
<!-- =========================================================== -->

<a id="predefined-data"></a>
# YIGO 预定义数据生成

## 概述

本 Skill 负责生成 YIGO 系统的**预定义数据 XML 文件**。预定义数据是与 Form 表单对应的初始化/升级数据，其 XML 结构直接映射 DataObject 的表结构（Table/Column），列值以 XML 属性形式承载。

> **核心原则**：每个预定义数据 XML 文件对应一个 DataObject（Form），文件名与 Form 的 Key 一致。XML 元素标签 = DataObject 中的 Table Key，XML 属性名 = Column Key，属性值 = 预定义的业务数据。

### 数据字典的引用关系与校验
预定义数据中如果存在**字典类型（Dict）**的字段值，务必保证其引用的字典数据在预定义数据文件中已存在，以维持数据的勾稽关系。

**勾稽关系追溯链路：**
1. 获取 Column 的 `DataElementKey` 属性，找到对应的 DataElement 定义。
2. 从 DataElement 的定义中，获取其关联的 `DomainKey`。
3. 从 Domain 的定义中，查找 `ItemKey`，这对应字典的数据对象（DataObject）的 Key。
4. 字典的数据对象 Key 与它对应的 Form Key 一致。
5. 验证该预定义字典数据文件（`{ItemKey}.xml`）中，是否存在一条记录，其 `Code` 字段的值与当前预定义数据中所填入的属性值相等。

**示例**：
在 `TCodeAuthorityObjectFieldValue.xml` 中，有一个属性 `AuthorityObjectID="S_USER_GRP"`：
- 字段 `AuthorityObjectID` 的 DataElement 是 `AuthorityObjectID`。
- DataElement `AuthorityObjectID` 对应的 Domain 是 `AuthorityObject`。
- Domain `AuthorityObject` 中配置了 `ItemKey="AuthorityObject"`。
- 去找数据对象为 `AuthorityObject` （进而 Form 也为 `AuthorityObject`）的预定义数据文件，即 `AuthorityObject.xml`（可能在此模块也可能在其它被依赖模块的预定义数据文件夹中）。
- 在 `AuthorityObject.xml` 中必须存在一条 `Code="S_USER_GRP"` 的记录，以此确保数据的完整性。

| 场景 | 目录 | 用途 |
|------|------|------|
| **系统初始化** | `initializeData/` | 新系统部署时导入的基础数据 |

## 目录位置

```
solutions/
├── erp-solution-core/
│   └── {module}config/          # 如 pmconfig, mmconfig, basisconfig
│       └── initializeData/       # ★ 初始化数据
│           └── {FormKey}.xml
```

## XML 整体结构

```xml
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<{FormKey}s>                              <!-- 根元素 = FormKey + "s" -->
    <{主表Key} 列1="值" 列2="值" ...>     <!-- 主表行 = 主表 Table Key -->
        <{子表Key}s>                      <!-- 子表容器 = 子表 Table Key + "s" -->
            <{子表Key} 列A="值" .../> 
        </{子表Key}s>
    </{主表Key}>
</{FormKey}s>
```

## 命名规则

### 根元素

根元素名 = **FormKey（DataObject Key） + "s"**（表示复数集合）

| FormKey | 根元素 |
|---------|--------|
| `PM_Strategy` | `<PM_Strategys>` |
| `PM_OrderType` | `<PM_OrderTypes>` |
| `Message` | `<Messages>` |
| `TCode` | `<TCodes>` |

### 行元素

行元素名 = **DataObject 中对应的 Table Key**（以 `E` 开头的表标识）

| Table Key | 行元素 |
|-----------|--------|
| `EPM_Strategy` | `<EPM_Strategy .../>` |
| `EPM_StrategyDtl` | `<EPM_StrategyDtl .../>` |
| `EGS_TCode` | `<EGS_TCode .../>` |
| `EGS_Message` | `<EGS_Message .../>` |

### 子表容器

子表行被包裹在容器元素中，容器名 = **子表 Table Key + "s"**

```xml
<EPM_Strategy ...>              <!-- 主表行 -->
    <EPM_StrategyDtls>           <!-- 子表容器 = EPM_StrategyDtl + "s" -->
        <EPM_StrategyDtl .../>   <!-- 子表行 -->
    </EPM_StrategyDtls>
</EPM_Strategy>
```

## 列值映射规则

DataObject Column 的 Key 作为 XML 属性名，值为预定义数据：

```xml
<!-- DataObject 定义 -->
<Column Key="Code" Caption="代码" DataElementKey="Code"/>
<Column Key="ClientID" Caption="集团" DataElementKey="ClientID"/>
<Column Key="Enable" Caption="启用标记" DefaultValue="1" DataElementKey="Enable"/>

<!-- 对应的预定义数据 -->
<EPM_ABCIndicator ClientID="000" Code="1_A" Enable="1" NodeType="0" .../>
```

> **注意**：系统字段（OID、SOID、POID、VERID、DVERID）在**初始化数据**中通常**省略**（由系统自动生成），但在**升级数据**中可能包含 `__OldPrimaryValue` 来标识记录。

## 多语言子表 (_Ts / _T)

对于包含 `SupportI18n="true"` 的 Name 字段，预定义数据通过专用的多语言子表承载：

```xml
<EPM_OrderType Code="PM01" ClientID="000" ...>
    <EPM_OrderType_Ts>                               <!-- 容器 = 主表Key + "_Ts" -->
        <EPM_OrderType_T Lang="zh-CN" Name="维护订单"/>  <!-- 行 = 主表Key + "_T" -->
        <EPM_OrderType_T Lang="en-US" Name="Maintain orders"/>
        <EPM_OrderType_T Lang="ja-JP" Name="保守オーダー"/>
    </EPM_OrderType_Ts>
</EPM_OrderType>
```

### 多语言子表命名规则

| 主表 Key | 多语言容器 | 多语言行 |
|----------|-----------|---------|
| `EPM_OrderType` | `EPM_OrderType_Ts` | `EPM_OrderType_T` |
| `EGS_TCode` | `EGS_TCode_Ts` | `EGS_TCode_T` |
| `EGS_Message` | `EGS_Message_Ts` | `EGS_Message_T` |

### 常用语言代码

| Lang | 语言 |
|------|------|
| `zh-CN` | 简体中文（**必填**） |
| `zh-CHT` | 繁体中文 |
| `en-US` | 英文 |
| `ja-JP` | 日文 |
| `ko-KR` | 韩文 |
| `de-DE` | 德文 |
| `fr-FR` | 法文 |
| `es-ES` | 西班牙文 |
| `pt-PT` | 葡萄牙文 |
| `ru-RU` | 俄文 |
| `ar-AE` | 阿拉伯文 |
| `th-TH` | 泰文 |
| `hu-HU` | 匈牙利文 |

> **最低要求**：至少包含 `zh-CN`。完整的初始化数据建议包含所有语言。

## 父子表关联（升级场景）

在升级数据中，父子表通过 `__OldPrimaryValue` 与 `POID` 关联：

```xml
<!-- 父表行：__OldPrimaryValue 标识此记录的主键 -->
<EAU_EntryTCodeRelation EntryTCode="QA33" EntryKey="subsys_QM_QA32_X2" 
                        RefFormKey="QM_QA32" __OldPrimaryValue="988124">
    <EAU_EntryOptTCodeRelations>
        <!-- 子表行：POID 引用父表的 __OldPrimaryValue -->
        <EAU_EntryOptTCodeRelation Sequence="1" TCode="QA02" 
                                   TCodeText="修改检验批" POID="988124"/>
        <EAU_EntryOptTCodeRelation Sequence="2" TCode="QA03" 
                                   TCodeText="显示检验批" POID="988124"/>
    </EAU_EntryOptTCodeRelations>
</EAU_EntryTCodeRelation>
```

### 关联规则

| 属性 | 所在位置 | 说明 |
|------|---------|------|
| `__OldPrimaryValue` | 父表行 | 标识父记录的主键值（数字 ID） |
| `POID` | 子表行 | 子记录指向父记录的外键，值 = 父表的 `__OldPrimaryValue` |

## 常见预定义数据类型

### 1. 字典类数据（Dict）

字典类 Form 的预定义数据最常见，通常包含 Code、Name（多语言）、ClientID 等字段。

```xml
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<PM_ABCIndicators>
    <EPM_ABCIndicator ABCIndicatorType="1" ClientID="000" Code="1_A" 
                      Enable="1" NodeType="0" Notes="" UseCode="A">
        <EPM_ABCIndicator_Ts>
            <EPM_ABCIndicator_T Lang="zh-CN" Name="A"/>
        </EPM_ABCIndicator_Ts>
    </EPM_ABCIndicator>
    <EPM_ABCIndicator ABCIndicatorType="1" ClientID="000" Code="1_B" 
                      Enable="1" NodeType="0" Notes="" UseCode="B">
        <EPM_ABCIndicator_Ts>
            <EPM_ABCIndicator_T Lang="zh-CN" Name="B"/>
        </EPM_ABCIndicator_Ts>
    </EPM_ABCIndicator>
</PM_ABCIndicators>
```

### 2. 带明细行的数据（父子表）

```xml
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<PM_Strategys>
    <EPM_Strategy CallHorizon="0" ClientID="000" Code="A" Enable="1" 
                  SchedulingIndicator="0" StrategyUnitID="mth">
        <EPM_StrategyDtls>
            <EPM_StrategyDtl CycleLength="1" CycleNotes="月" 
                             PackageNo="1" PackageUnitID="mth"/>
            <EPM_StrategyDtl CycleLength="3" CycleNotes="季度" 
                             PackageNo="2" PackageUnitID="mth"/>
        </EPM_StrategyDtls>
        <EPM_Strategy_Ts>
            <EPM_Strategy_T Lang="zh-CN" Name="A"/>
            <EPM_Strategy_T Lang="en-US" Name="A"/>
        </EPM_Strategy_Ts>
    </EPM_Strategy>
</PM_Strategys>
```

### 3. 事务码数据（TCode）—— 多级子表

```xml
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<TCodes>
    <EGS_TCode Code="CL01" Enable="1" NodeType="0" Notes="" 
               OperateType="1" RefTCode="CL01" TransactionCodePackageID="MM">
        <EGS_TCode_Ts>
            <EGS_TCode_T Lang="zh-CN" Name="分类创建"/>
        </EGS_TCode_Ts>
        <EGS_TCode_DefaultCheckObjectss>
            <EGS_TCode_DefaultCheckObjects AuthorityObjectID="C_KLAH_BKP"/>
        </EGS_TCode_DefaultCheckObjectss>
        <EGS_TCode_ObjectFieldValuess>
            <EGS_TCode_ObjectFieldValues AuthorityFieldID="ACTVT" 
                                         AuthorityFieldValue="01"/>
        </EGS_TCode_ObjectFieldValuess>
        <EGS_TCode_FormLists>
            <EGS_TCode_FormList FormKey="Classification"/>
        </EGS_TCode_FormLists>
    </EGS_TCode>
</TCodes>
```

### 4. 消息类数据（Message）

```xml
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<Messages>
    <EGS_Message ClientID="000" Code="GLVCHFMCOSETTLE006" Enable="1" 
                 MessageClassID="GLVCHFMCOSETTLE" MsgLongText="" 
                 MsgType="I" NodeType="0" Notes="" UseCode="006">
        <EGS_Message_Ts>
            <EGS_Message_T Lang="zh-CN" Name="成本控制生产订单结算"/>
        </EGS_Message_Ts>
    </EGS_Message>
</Messages>
```

### 5. 纯扁平数据（无子表）

```xml
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<TCodeAuthorityObjectFieldDefaultValues>
    <EAU_TCodeAuthObjectDefRel AuthorityObjectID="Y_PLANT" 
                                CheckType="Y" TCodeID="OIOB"/>
</TCodeAuthorityObjectFieldDefaultValues>
```

## 生成步骤

1. **确定 FormKey**：获取目标数据对应的 Form Key（DataObject Key）
2. **获取 DataObject 表结构**：从 DataObject XML 中提取 TableCollection 的所有 Table 和 Column 定义
3. **确定根元素**：根元素名 = `FormKey + "s"`
4. **生成行数据**：每个数据行 = 主表 Table Key 作为元素名，Column Key 作为属性名
5. **处理子表**：如有子表，创建子表容器（子表Key + "s"），内嵌子表行
6. **处理多语言**：如有 `SupportI18n` 字段，生成 `_Ts` / `_T` 多语言子表
7. **设置关联**（仅升级）：父表加 `__OldPrimaryValue`，子表加 `POID` 引用

## 字典数据常用系统字段

字典类预定义数据中，以下字段通常**需要填写**：

| 字段（属性） | 说明 | 典型值 |
|-------------|------|--------|
| `Code` | 字典代码（主键） | 业务编码 |
| `ClientID` | 集团代码 | `"000"` |
| `Enable` | 启用标记 | `"1"` |
| `NodeType` | 节点类型 | `"0"` |
| `Notes` | 备注 | `""` |

以下字段通常**省略**（系统自动处理）：

| 字段 | 说明 |
|------|------|
| `OID` | 对象标识 |
| `SOID` | 主对象标识 |
| `POID` | 父对象标识（初始化时省略） |
| `VERID` | 对象版本 |
| `DVERID` | 对象明细版本 |
| `TLeft` / `TRight` | 树形结构左右值 |
| `Creator` / `CreateTime` | 创建人/时间 |
| `Modifier` / `ModifyTime` | 修改人/时间 |

## 校验规则

1. **文件名必须与 FormKey 一致**：`{FormKey}.xml`
2. **根元素名 = FormKey + "s"**
3. **行元素名 = DataObject 中的 Table Key**
4. **属性名 = DataObject 中的 Column Key**
5. **XML 声明**：必须以 `<?xml version="1.0" encoding="UTF-8" standalone="no"?>` 开头
6. **多语言至少包含 `zh-CN`**
7. **升级数据中有子表时**：父表必须有 `__OldPrimaryValue`，子表必须有对应的 `POID`
8. **属性值中的特殊字符**需转义：`&` → `&amp;`，`<` → `&lt;`，`>` → `&gt;`，`"` → `&quot;`

## 与其他 Skill 的配合

| 配合 Skill | 关系说明 |
|------------|----------|
| [DataObject 生成](#dataobject-generator) | 预定义数据的表结构（元素/属性）完全映射 DataObject 的 Table/Column 定义 |
| [Form 脚手架](#form-scaffold) | 预定义数据的 FormKey 对应 Form 的 Key |
| [DataElement 生成](#dataelement-generator) | 通过 DataElement 可了解字段的数据类型，确保属性值格式正确 |
| [Domain 生成](#domain-generator) | 通过 Domain 可获知 ComboBox 枚举值，预定义数据中的值必须在枚举范围内 |

### 生成流程

生成预定义数据前，需先确保以下已就绪：

1. **DataObject**（[DataObject 生成](#dataobject-generator)）→ 获取表结构和列定义
2. **Domain**（[Domain 生成](#domain-generator)）→ 确认枚举值范围，数据类型
3. **预定义数据**（本 Skill）→ 基于表结构填充业务数据


---

<a id="sap-permission-data"></a>
# SAP 权限相关预定义数据生成

## 概述

本 Skill 基于 [预定义数据生成](#predefined-data)（YIGO 预定义数据生成规范），专门针对 **SAP 权限体系** 的相关表单（非报表）生成初始化数据或升级数据。
本 Skill 负责在对应的模块（如 `erp-solution-core/authorityConfig/initializeData/` 或其它业务模块的初始化数据目录）下提供配置数据。

> **核心原则**：SAP 的权限体系核心对象（权限对象类、权限对象、权限字段、作业值、事务码、SU24缺省值）均有对应的 Form 和 DataObject，生成的 XML 须符合 DataObject 的字段映射。

---

## 核心权限模型映射表

在补充 SAP 权限数据时，需参考以下映射关系组装 XML：

| SAP 概念 | 对应 Form Key | 对应 DataObject Key | Root 元素名 | 表名 (Row 元素名) | 核心字段说明 |
|---|---|---|---|---|---|
| **权限对象类 (TOBC)** | `AuthorityObjectClass` | `AuthorityObjectClass` | `<AuthorityObjectClasss>` | `<EAU_AuthorityObjectClass>` | `Code` (代码), `Name` (多语言名称) |
| **权限对象 (TOBJ)** | `AuthorityObject` | `AuthorityObject` | `<AuthorityObjects>` | `<EAU_AuthorityObject>` | `Code` (代码), `AuthorityObjectClassID` (权限类), `AuthorityFieldID01`~`10` (引用权限字段) |
| **权限字段 (AUTH_FLD)** | `AuthorityField` | `AuthorityField` | `<AuthorityFields>` | `<EAU_AuthorityField>` | `Code` (字段名), `DataElementKey` (数据元素) |
| **权限作业值 (TACT)** | `AuthorityActivity` | `AuthorityActivity` | `<AuthorityActivitys>` | `<EAU_AuthorityActivity>` | `Code` (作业值), `Name` (描述) |
| **事务码 (TCode)** | `TCode` | `TCode` | `<TCodes>` | `<EGS_TCode>` | `Code` (事务码), `TransactionCodePackageID` (包) |
| **权限组织变量** | `AuthorityOrgVariable` | `AuthorityOrgVariable` | `<AuthorityOrgVariables>` | `<EAU_AuthorityOrgVariable>` | `Code` (代码), `Name` (多语言名称) |
| **权限对象作业值** | `AuthorityObjectActivity` | `AuthorityObjectActivity` | `<AuthorityObjectActivitys>` | `<EAU_AuthorityObjectActivity>` | `AuthorityObjectID` (权限对象), `AuthorityActivityID` (作业值) |
| **菜单与事务码关系** | `EntryTCodeRelation` | `EntryTCodeRelation` | `<EntryTCodeRelations>` | `<EAU_EntryTCodeRelation>` | `EntryKey` (菜单标识), `EntryTCode` (对应的事务码) |
| **事务码权限缺省值 (标准模板)**| `TCodeAuthorityObjectFieldDefaultValue`| `TCodeAuthorityFieldDefaultValue` | `<TCodeAuthorityFieldDefaultValues>` | `<EAU_TCodeAuthObjectDefRel>` | `TCodeID` (事务代码), `AuthorityObjectID` (权限对象) |
| **事务码权限缺省值 (项目可改)**| `TCodeAuthorityObjectFieldValue` | `TCodeAuthorityFieldValue` | `<TCodeAuthorityFieldValues>` | `<EAU_TCodeAuthorityObjectRelDtl>`| 同上，主表变为 `EAU_TCodeAuthorityObjectRelDtl` |

---

## 常用生成示例

### 1. 权限对象类 (AuthorityObjectClass)

```xml
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<AuthorityObjectClasss>
    <EAU_AuthorityObjectClass ClientID="000" Code="MM_M" Enable="1" NodeType="0" Notes="">
        <EAU_AuthorityObjectClass_Ts>
            <EAU_AuthorityObjectClass_T Lang="zh-CN" Name="物料管理：主数据"/>
            <EAU_AuthorityObjectClass_T Lang="en-US" Name="Materials Management: Master Data"/>
        </EAU_AuthorityObjectClass_Ts>
    </EAU_AuthorityObjectClass>
</AuthorityObjectClasss>
```

### 2. 权限字段 (AuthorityField)

> 需注意：权限字段通常会配置其对应的数据元素 `DataElementKey`。

```xml
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<AuthorityFields>
    <EAU_AuthorityField ClientID="000" Code="ACTVT" Enable="1" NodeType="0" DataElementKey="AuthorityActivity" IsEmptyHasAuthority="1" Notes="作业">
        <EAU_AuthorityField_Ts>
            <EAU_AuthorityField_T Lang="zh-CN" Name="作业"/>
        </EAU_AuthorityField_Ts>
    </EAU_AuthorityField>
</AuthorityFields>
```

### 3. 权限对象 (AuthorityObject)

> 权限对象一般需引用权限对象类 (`AuthorityObjectClassID`) 以及 1-10 个权限字段 (`AuthorityFieldID01` 等)。

```xml
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<AuthorityObjects>
    <EAU_AuthorityObject ClientID="000" Code="M_MATE_MAR" Enable="1" NodeType="0" 
                         AuthorityObjectClassID="MM_M" AuthorityFieldID01="ACTVT" AuthorityFieldID02="BEGRU">
        <EAU_AuthorityObject_Ts>
            <EAU_AuthorityObject_T Lang="zh-CN" Name="物料主记录：物料类型"/>
        </EAU_AuthorityObject_Ts>
    </EAU_AuthorityObject>
</AuthorityObjects>
```

### 4. 权限组织变量 (AuthorityOrgVariable)

> 定义权限组织相关的变量，补充组织权限时使用。

```xml
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<AuthorityOrgVariables>
    <EAU_AuthorityOrgVariable ClientID="000" Code="WERKS" Enable="1" NodeType="0">
        <EAU_AuthorityOrgVariable_Ts>
            <EAU_AuthorityOrgVariable_T Lang="zh-CN" Name="工厂"/>
        </EAU_AuthorityOrgVariable_Ts>
    </EAU_AuthorityOrgVariable>
</AuthorityOrgVariables>
```

### 5. 权限对象可使用的作业值 (AuthorityObjectActivity)

> 定义权限对象能使用哪些权限作业值字段。用于初始化绑定 AuthObject 和 Activity。

```xml
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<AuthorityObjectActivitys>
    <!-- M_MATE_MAR 权限对象允许的作业值: 01, 02, 03 -->
    <EAU_AuthorityObjectActivity AuthorityObjectID="M_MATE_MAR" AuthorityActivityID="01"/>
    <EAU_AuthorityObjectActivity AuthorityObjectID="M_MATE_MAR" AuthorityActivityID="02"/>
    <EAU_AuthorityObjectActivity AuthorityObjectID="M_MATE_MAR" AuthorityActivityID="03"/>
</AuthorityObjectActivitys>
```

### 6. 菜单与事务码关系 (EntryTCodeRelation)

> 将菜单(Entry)和主权限事务码绑定，用于在新增菜单时默认赋予其对应的底层事务码。
> 可选子表 `EAU_EntryOptTCodeRelation` 用于绑定该菜单操作下的其他事务码。

```xml
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<EntryTCodeRelations>
    <!-- EntryKey 中通常会有 __OldPrimaryValue 用于升级模式映射 -->
    <EAU_EntryTCodeRelation EntryKey="subsys_MM_MM01" EntryTCode="MM01" RefFormKey="Material" __OldPrimaryValue="10001">
        <EAU_EntryOptTCodeRelations>
            <EAU_EntryOptTCodeRelation Sequence="1" TCode="MM02" TCodeText="修改物料" POID="10001"/>
            <EAU_EntryOptTCodeRelation Sequence="2" TCode="MM03" TCodeText="显示物料" POID="10001"/>
        </EAU_EntryOptTCodeRelations>
    </EAU_EntryTCodeRelation>
</EntryTCodeRelations>
```

### 7. 事务码权限缺省值 (SU24)

事务码权限缺省值存在两份相似但用途不同的定义：
1. **TCodeAuthorityObjectFieldDefaultValue**: 系统标准参考模板 (不允许项目直接修改)。主表 `EAU_TCodeAuthObjectDefRel`，子表 `EAU_TCodeValidAuthFieldDef`。
2. **TCodeAuthorityObjectFieldValue**: 项目可修改版本 (作为项目实施时的独立隔离态)。主表 `EAU_TCodeAuthorityObjectRelDtl`，子表 `EAU_TCodeValidAuthFieldValue`。二者映射和结构相似但使用不同的 XML 标签和表名。

以项目可修版本的 **TCodeAuthorityObjectFieldValue** 为例：

```xml
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<!-- 注意这里的 Root、主子表名字 与标准不可改模板版不完全相同 -->
<TCodeAuthorityFieldValues>
    <EAU_TCodeAuthorityObjectRelDtl CheckType="Y" AuthorityObjectID="M_MATE_MAR" TCodeID="MM01" IsSpecialCheck="0">
        <!-- 权限字段默认值子表 -->
        <EAU_TCodeValidAuthFieldValues>
            <EAU_TCodeValidAuthFieldValue AuthorityFieldID="ACTVT" AuthorityFieldValue="01"/>
            <EAU_TCodeValidAuthFieldValue AuthorityFieldID="ACTVT" AuthorityFieldValue="02"/>
            <EAU_TCodeValidAuthFieldValue AuthorityFieldID="ACTVT" AuthorityFieldValue="03"/>
        </EAU_TCodeValidAuthFieldValues>
        <!-- 权限字段属性子表 (可选) -->
        <EAU_TCodeValidAuthFieldProps>
            <EAU_TCodeValidAuthFieldProp AuthorityFieldID="BEGRU" DataElementKey="MaterialGroup"/>
        </EAU_TCodeValidAuthFieldProps>
    </EAU_TCodeAuthorityObjectRelDtl>
</TCodeAuthorityFieldValues>
```

> **字段说明：**
> - **CheckType (检查类型)**: `Y` (检查/保持), `N` (不检查), `U` (未维护)。通常使用 `Y`。
> - **AuthorityFieldValue**: 权限字段具体的值，如对于 `ACTVT` 可能是 `01`(创建), `02`(修改), `03`(显示)。

### 8. 事务码 (TCode)

> 定义系统的事务码，及其默认的权限检查对象、挂载的表单等。TCode数据通常放在各个业务模块的基础预定义数据中。

```xml
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<TCodes>
    <EGS_TCode Code="MM01" Enable="1" NodeType="0" Notes="" OperateType="1" TransactionCodePackageID="MM">
        <EGS_TCode_Ts>
            <EGS_TCode_T Lang="zh-CN" Name="创建物料"/>
        </EGS_TCode_Ts>
        <!-- 默认检查的权限对象集合 -->
        <EGS_TCode_DefaultCheckObjectss>
            <EGS_TCode_DefaultCheckObjects Sequence="1" AuthorityObjectID="M_MATE_MAR">
                <!-- 权限字段值集合 (挂在默认检查对象下) -->
                <EGS_TCode_ObjectFieldValuess>
                    <EGS_TCode_ObjectFieldValues Sequence="1" AuthorityFieldID="ACTVT" AuthorityFieldValue="01"/>
                </EGS_TCode_ObjectFieldValuess>
            </EGS_TCode_DefaultCheckObjects>
        </EGS_TCode_DefaultCheckObjectss>
        <!-- 绑定的表单集合 -->
        <EGS_TCode_FormLists>
            <EGS_TCode_FormList Sequence="1" FormKey="Material"/>
        </EGS_TCode_FormLists>
    </EGS_TCode>
</TCodes>
```

---

## 前置动作与校验规则

1.  **依赖字典的存在性**：在写入 `TCodeAuthorityFieldDefaultValues` 时，务必保证引用的 `AuthorityObjectID`、`AuthorityFieldID` 以及 `TCodeID` 在系统中已有数据或同时提供预定义数据。
2.  **遵守 YIGO 预定义生成规范**：本 Skill 在生成 XML 时，遵循 [预定义数据生成](#predefined-data) 中的所有原则（主外键忽略 `OID` 等，多语言表 `_Ts`，外键写 `Code` 值等）。
3.  **定位目录**：这类字典数据通常是在各个系统模块（如 `pmconfig/initializeData/` 等）存放，以配合该模块的 TCode 初始化。

---

<!-- =========================================================== -->
# 附录：章节交叉引用索引
<!-- =========================================================== -->

<a id="cross-reference-index"></a>

| 章节 | 锚点 ID | 原始文件 |
|------|---------|----------|
| YIGO 平台架构 | #architecture | yigo-erp-architecture.md |
| YIGO ERP 工作流 | #workflow | yigo-erp-architecture.md |
| 角色定义 | #role-definition | yigo-erp-architecture.md |
| 模块命名规范 | #module-naming | module-naming-conventions.md |
| BNF 语法 | #bnf-grammar | peixw.bnf |
| Domain 数据域生成 | #domain-generator | yigo-domain-generator |
| DataElement 生成 | #dataelement-generator | yigo-dataelement-generator |
| DataObject 生成 | #dataobject-generator | yigo-dataobject-generator |
| Form 脚手架 | #form-scaffold | yigo-form-scaffold |
| 面板布局 | #panel-layout | yigo-panel-layout |
| UI 控件生成 | #control-generator | yigo-control-generator |
| Grid 表格生成 | #grid-generator | yigo-grid-generator |
| 操作与脚本 | #operation-script | yigo-operation-script |
| 表达式书写 | #expression-writer | yigo-expression-writer |
| Java 二次开发 | #java-customization | yigo-java-customization |
| 预定义数据生成 | #predefined-data | yigo-predefined-data-generator |
| SAP 权限数据 | #sap-permission-data | sap-permission-data-generator |
| 字典表单模板 | #template-dict-form | templates/dict-form.md |
| 后台配置表模板 | #template-backend-config | templates/backend-config-form.md |
| 单界面报表模板 | #template-single-report | templates/single-report-form.md |
| 双表单报表模板 | #template-dual-report | templates/dual-report-form.md |