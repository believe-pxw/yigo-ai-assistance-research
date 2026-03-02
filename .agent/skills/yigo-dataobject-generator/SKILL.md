---
name: yigo-dataobject-generator
description: 生成 YIGO DataObject XML 配置，包含 TableCollection、Column、Relation、IndexCollection 等数据模型定义
---

# YIGO DataObject 数据对象生成

## 概述

本 Skill 负责生成 YIGO 系统的**数据对象（DataObject）XML 配置**。DataObject 定义了表单的数据模型，包含表集合、列定义、表间关系、嵌入表和索引等。

## XSD 参考文件

- 主文件：[DataObject.xsd](file:///d:/Workbench/idea/yigo-ai-assistance-research/resource/xsd/xsd/DataObject.xsd)
- 详细定义：[DataObjectDefine.xsd](file:///d:/Workbench/idea/yigo-ai-assistance-research/resource/xsd/xsd/element/complex/DataObjectDefine.xsd)
- 脚本定义：[BaseScriptDefinition.xsd](file:///d:/Workbench/idea/yigo-ai-assistance-research/resource/xsd/xsd/element/complex/BaseScriptDefinition.xsd)

## DataObject 完整结构

```xml
<DataObject Key="数据对象标识" Caption="名称" PrimaryType="Entity" 
            SecondaryType="Normal" PrimaryTableKey="主表Key">
    <!-- 1. 表集合 -->
    <TableCollection>
        <Table Key="表标识" Caption="表名称" TableMode="" PrimaryKey="主键字段" Persist="true">
            <Column Key="列标识" Caption="列名称" DataType="String" Length="50" />
            <Column Key="Amount" DataType="Decimal" Precision="18" Scale="2" />
            <TableFilter Type="Const">过滤条件</TableFilter>
            <ParameterCollection>
                <Parameter FieldKey="字段" TargetColumn="目标列" SourceType="Field" />
            </ParameterCollection>
            <Statement Type="Formula">SQL或公式</Statement>
            <IndexCollection>
                <Index Key="idx1" Columns="col1,col2" IsUnique="true" />
            </IndexCollection>
        </Table>
    </TableCollection>

    <!-- 2. 关系定义 -->
    <Relation>
        <Layer ItemKey="字典项" ColumnKey="列" TableKey="表" Relation="关系表达式" />
    </Relation>

    <!-- 3. 嵌入表集合 -->
    <EmbedTableCollection>
        <EmbedTable ObjectKey="对象标识" TableKeys="表1,表2" />
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
| `NoPrefix` | string | ❌ | 单据编号前缀 |
| `DisplayFields` | string | ❌ | 字典显示字段 |
| `DropviewFields` | string | ❌ | 字典下拉框显示列 |
| `QueryFields` | string | ❌ | 字典模糊查询字段 |
| `MaintainDict` | Boolean | ❌ | 是否维护字典的 tleft tright |
| `IndexPrefix` | string | ❌ | 索引前缀 |
| `Version` | string | ❌ | 配置版本 |
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
| `OrderBy` | string | ❌ | 排序方式 |
| `GroupBy` | string | ❌ | 分组方式 |
| `Formula` | Formula | ❌ | 表达式 |
| `Impl` | string | ❌ | 实现类 |
| `ParentKey` | string | ❌ | 父表标识 |
| `DBTableName` | string(30) | ❌ | 数据库表名（当与 Key 不同时） |
| `LazyLoad` | Boolean | ❌ | 是否延迟加载 |
| `IndexPrefix` | string | ❌ | 索引前缀 |
| `RefreshFilter` | Boolean | ❌ | 是否刷新过滤条件 |

## Column（列）属性

| 属性 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `Key` | string(50) | ✅ | 列标识（持久化列 ≤ 30 字符） |
| `Caption` | string | ❌ | 列名称 |
| `DataType` | 枚举 | ❌ | 数据类型（见 AttributeRestriction.xsd） |
| `Length` | int(≥0) | ❌ | 长度（有 DataElementKey 时不能配置） |
| `Precision` | int(≥0) | ❌ | 精度（有 DataElementKey 时不能配置） |
| `Scale` | int(≥0) | ❌ | 小数位数（有 DataElementKey 时不能配置） |
| `DefaultValue` | string | ❌ | 默认值 |
| `DataElementKey` | string | ❌ | 引用的数据元素 |
| `IsPrimaryKey` | Boolean | ❌ | 是否主键 |
| `IsPrimary` | Boolean | ❌ | 是否主键（旧版） |
| `Persist` | Boolean | ❌ | 是否持久化 |
| `DBColumnName` | string | ❌ | 数据库列名 |
| `Cache` | Boolean | ❌ | 是否缓存 |
| `SortType` | `Asc`/`Desc` | ❌ | 排序类型 |
| `ItemKey` | string(50) | ❌ | 字典项标识 |
| `RefCol` | string | ❌ | 引用列 |
| `RefItemKeyCol` | string(50) | ❌ | 引用字典项列 |
| `CodeColumnKey` | string | ❌ | 编码列标识 |
| `SupportI18n` | Boolean | ❌ | 是否支持国际化 |
| `Expand` | Boolean | ❌ | 是否扩展 |
| `NeedRights` | Boolean | ❌ | 是否需要权限 |
| `IgnoreSave` | Boolean | ❌ | 是否忽略保存 |
| `IgnoreQuery` | Boolean | ❌ | 是否忽略查询 |
| `AccessControl` | Boolean | ❌ | 访问控制 |
| `GroupType` | 枚举 | ❌ | 分组类型（迁移表用）：`Discrete`/`Period`/`PeriodGroup` |
| `SplitType` | `Period` | ❌ | 拆分类型（迁移表用） |
| `PeriodImpl` | string | ❌ | 期间实现类（迁移表用） |

## 校验规则

1. **持久化的表** Key 长度 ≤ 30 字符
2. **持久化的列** Key 长度 ≤ 30 字符，否则 ≤ 50 字符
3. **存在 DataElementKey 时**不能配置 `Precision`、`Scale`、`Length`
4. **迁移表专有属性**（`GroupType`、`SplitType`、`PeriodImpl`）只有 `SecondaryType='Migration'` 时允许配置
5. **MigrationUpdateStrategy** 只有迁移表（`SecondaryType='Migration'`）允许配置
6. **DictCacheCheckMode** 只有字典表单（`FormType='Dict'`）允许配置
7. **Column Key 唯一**：同一 Table 下的 Column Key 不能重复

## 使用示例

### 示例 1：标准单据数据对象（采购订单）

```xml
<DataObject Key="PurchaseOrder" Caption="采购订单" PrimaryType="Entity" 
            SecondaryType="Normal" PrimaryTableKey="PurchaseOrder">
    <TableCollection>
        <!-- 主表 -->
        <Table Key="PurchaseOrder" Caption="采购订单主表" Persist="true">
            <Column Key="PONo" Caption="订单编号" DataType="String" Length="30" IsPrimaryKey="true" />
            <Column Key="PODate" Caption="订单日期" DataType="Date" />
            <Column Key="SupplierID" Caption="供应商" DataType="String" Length="30" ItemKey="Supplier" />
            <Column Key="TotalAmount" Caption="总金额" DataType="Decimal" Precision="18" Scale="2" />
            <Column Key="Status" Caption="状态" DataType="Int" DefaultValue="0" />
            <Column Key="Remark" Caption="备注" DataType="String" Length="200" />
        </Table>
        <!-- 明细表 -->
        <Table Key="PurchaseOrderDtl" Caption="采购订单明细" TableMode="Detail" Persist="true" ParentKey="PurchaseOrder">
            <Column Key="LineNo" Caption="行号" DataType="Int" IsPrimaryKey="true" />
            <Column Key="MaterialID" Caption="物料" DataType="String" Length="30" ItemKey="Material" />
            <Column Key="Qty" Caption="数量" DataType="Decimal" Precision="18" Scale="4" />
            <Column Key="Price" Caption="单价" DataType="Decimal" Precision="18" Scale="4" />
            <Column Key="Amount" Caption="金额" DataType="Decimal" Precision="18" Scale="2" />
        </Table>
    </TableCollection>
    <Relation>
        <Layer ItemKey="Supplier" ColumnKey="SupplierID" TableKey="PurchaseOrder" />
        <Layer ItemKey="Material" ColumnKey="MaterialID" TableKey="PurchaseOrderDtl" />
    </Relation>
</DataObject>
```

### 示例 2：字典数据对象

```xml
<DataObject Key="CurrencyDict" Caption="币别字典" PrimaryType="Entity" 
            SecondaryType="Dict" PrimaryTableKey="Currency"
            DisplayFields="CurrencyName" QueryFields="CurrencyCode,CurrencyName">
    <TableCollection>
        <Table Key="Currency" Caption="币别" Persist="true">
            <Column Key="CurrencyCode" Caption="币别编码" DataType="String" Length="10" IsPrimaryKey="true" />
            <Column Key="CurrencyName" Caption="币别名称" DataType="String" Length="50" />
            <Column Key="Symbol" Caption="符号" DataType="String" Length="5" />
        </Table>
    </TableCollection>
</DataObject>
```

### 示例 3：带查询表来源的虚拟表

```xml
<DataObject Key="SalesReport" Caption="销售报表" PrimaryType="Entity" PrimaryTableKey="SalesData">
    <TableCollection>
        <Table Key="SalesData" Caption="销售数据" SourceType="Query" Persist="false">
            <Column Key="ProductID" Caption="产品" DataType="String" Length="30" />
            <Column Key="SalesQty" Caption="销售数量" DataType="Decimal" Precision="18" Scale="2" />
            <Statement Type="Sql">SELECT ProductID, SUM(Qty) AS SalesQty FROM SalesDetail GROUP BY ProductID</Statement>
        </Table>
    </TableCollection>
</DataObject>
```

## 与其他 Skill 的配合

- 在 Form XML 中，DataObject 嵌套在 `<DataSource>` 下 → 配合 `yigo-form-scaffold`
- DataObject 中的 Table/Column 定义了数据字段 → 供 `yigo-control-generator` 和 `yigo-grid-generator` 做数据绑定（DataBinding）
- Column 中的 `ItemKey` 指向 DomainDef 中的字典定义 → 配合 `yigo-domain-generator`（未来 Skill）
