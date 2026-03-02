package com.bokesoft.yes.mid.cmd.richdocument.strut;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.bokesoft.yes.common.log.LogSvr;
import com.bokesoft.yes.common.struct.StringHashMap;
import com.bokesoft.yes.mid.authority.util.AuthorityCheckUtil;
import com.bokesoft.yes.mid.authority.util.AuthorityParaUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import com.bokesoft.yes.erp.message.base.context.MessageContext;
import com.bokesoft.yes.erp.message.base.model.MessageData;
import com.bokesoft.erp.performance.Performance;
import com.bokesoft.yes.common.util.StringUtil;
import com.bokesoft.yes.mid.cmd.richdocument.strut.uiprocess.CalcUtil;
import com.bokesoft.yes.mid.dbcache.preload.PreLoadData;
import com.bokesoft.yes.mid.io.doc.TableRightsFilter;
import com.bokesoft.yes.struct.document.DocumentJSONConstants;
import com.bokesoft.yes.util.DataConstant;
import com.bokesoft.yigo.common.def.ControlType;
import com.bokesoft.yigo.common.def.DataType;
import com.bokesoft.yigo.common.def.GridTreeType;
import com.bokesoft.yigo.common.def.SystemField;
import com.bokesoft.yigo.common.def.TableMode;
import com.bokesoft.yigo.common.util.TypeConvertor;
import com.bokesoft.yigo.meta.dataobject.MetaColumn;
import com.bokesoft.yigo.meta.dataobject.MetaDataObject;
import com.bokesoft.yigo.meta.dataobject.MetaDataSource;
import com.bokesoft.yigo.meta.dataobject.MetaTable;
import com.bokesoft.yigo.meta.dataobject.MetaTableCollection;
import com.bokesoft.yigo.meta.form.MetaForm;
import com.bokesoft.yigo.meta.form.component.MetaComponent;
import com.bokesoft.yigo.meta.form.component.grid.MetaGrid;
import com.bokesoft.yigo.meta.form.component.grid.MetaGridCell;
import com.bokesoft.yigo.meta.form.component.grid.MetaGridRow;
import com.bokesoft.yigo.meta.form.component.grid.MetaGridRowCollection;
import com.bokesoft.yigo.meta.form.component.grid.MetaRowTree;
import com.bokesoft.yigo.mid.base.DefaultContext;
import com.bokesoft.yigo.struct.datatable.ColumnInfo;
import com.bokesoft.yigo.struct.datatable.DataTable;
import com.bokesoft.yigo.struct.datatable.DataTableMetaData;
import com.bokesoft.yigo.struct.document.Document;
import com.bokesoft.yigo.struct.usrpara.Paras;

/**
 * 记录脏数据的数据对象，最后将脏数据返回客户端显示
 */
public class DocumentRecordDirty extends RichDocument {

	/** 除空白行外的所有脏数据 */
	private Set<FieldLocation<?>> dirtyFieldLocations = new LinkedHashSet<FieldLocation<?>>();
	/** 空白行的脏数据 */
	private Map<String, List<FieldLocation<?>>> emptyGridRowDirtyValues = new HashMap<String, List<FieldLocation<?>>>();

	public List<UICommand> getUiCommands() {
		return uiCommands;
	}

	public void setUiCommands(List<UICommand> uiCommands) {
		this.uiCommands = uiCommands;
	}

	public DocumentRecordDirty(MetaForm metaForm) {
		super(metaForm, true);
		this.setFullData();
	}

	public DocumentRecordDirty(MetaForm metaForm, boolean isNewDocumentID) {
		super(metaForm, isNewDocumentID);
	}
	/**
	 * 根据界面转来的json对象构建document对象
	 * @param context
	 * @param jsonDoc
	 * @param formKey
	 * @return
	 * @throws Throwable
	 */
	private static DocumentRecordDirty getDocumentFromJSON(RichDocumentContext context, JSONObject jsonDoc, String formKey)
			throws Throwable {
		MetaForm metaForm = context.getVE().getMetaFactory().getMetaForm(formKey);
		DocumentRecordDirty richDocument = new DocumentRecordDirty(metaForm, false);
		richDocument.setContext(context);
		if (jsonDoc != null) {
			richDocument.fromJSON(jsonDoc);
		}
		return richDocument;
	}

	/**
	 * 将Document对象转化为DocumentRecordDirty对象，目前仅用Yigo2.0原生态的中间层服务调用ERP的二次开发
	 * TODO: 这个方法目前只处理了tableList，对expandData的处理是错误的，还有更多的Document成员没有正式同步
	 * @param doc
	 * @param metaForm
	 * @return
	 * @throws Throwable
	 */
	public static DocumentRecordDirty getDocumentFromDoc(Document doc, MetaForm metaForm) throws Throwable {
		DocumentRecordDirty richDocument = new DocumentRecordDirty(metaForm, false);
		richDocument.setOID(doc.getOID());
		richDocument.setPOID(doc.getPOID());
		richDocument.setVERID(doc.getVERID());
		richDocument.setDVERID(doc.getDVERID());

		MetaTableCollection metaTableCollection = doc.getMetaDataObject().getTableCollection();
		Iterator<MetaTable> it = metaTableCollection.iterator();
		MetaTable metaTable = null;
		DataTable table = null;
		while (it.hasNext()) {
			metaTable = it.next();
			if (metaTable.isHidden()) {
                continue;
            }
			table = doc.get(metaTable.getKey());
			if (table == null) {
				// 删除操作的Document中是不包含不保存数据库的DataTable的,具体可参考
				// com.bokesoft.yes.mid.cmd.data.DeleteFormDataCmd.doCmd(DefaultContext)方法
				continue;
			}
			richDocument.add(metaTable.getKey(), table);
		}

		richDocument.putAttr("state", String.valueOf(doc.getState()));
		richDocument.setDocumentType(doc.getDocumentType());
//		richDocument.setDocumentID(doc.getDocumentID());

		String key = null;
		Object value = null;
		for (Entry<String, Object> e : doc.getAllExpandData().entrySet()) {
			key = e.getKey();
			value = e.getValue();
			richDocument.putExpandData(key, value);
		}
		return richDocument;
	}

	/**
	 * 根据界面转来的json字符串构建document对象
	 * @param context
	 * @param jsonDocString
	 * @param formKey
	 * @return
	 * @throws Throwable
	 */
	public static DocumentRecordDirty getDocumentFromString(RichDocumentContext context, String jsonDocString, String formKey)
			throws Throwable {
		DocumentRecordDirty document = null;
		JSONObject jsonDoc = null;
		if (jsonDocString != null && jsonDocString.length() > 0) {
			jsonDoc = new JSONObject(jsonDocString);
			if(!jsonDoc.has(DocumentJSONConstants.DOCUMENT_TABLELIST)){
				jsonDoc.put(DocumentJSONConstants.DOCUMENT_TABLELIST, new JSONArray());
			}
		}
		document = getDocumentFromJSON(context, jsonDoc, formKey);
		document.setContext(context);
		return document;
	}

	@Override
	protected boolean setValueNoChanged(FieldLocation<?> fieldLocation, Object value) throws Throwable {
		boolean result = super.setValueNoChanged(fieldLocation, value);
		if (result  && !this.isFullData() ) {
			if (fieldLocation instanceof GridEmptyRowFieldLocation) {
				final IDLookup idLookup = IDLookup.getIDLookup(getMetaForm());
				final String gridKey = idLookup.getGridKeyByFieldKey(fieldLocation.getKey());
				List<FieldLocation<?>> rowDirtyLocations = emptyGridRowDirtyValues.get(gridKey);
				if (rowDirtyLocations == null) {
					rowDirtyLocations = new ArrayList<>();
					emptyGridRowDirtyValues.put(gridKey, rowDirtyLocations);
				}
				if (!rowDirtyLocations.contains(fieldLocation)) {
					rowDirtyLocations.add(fieldLocation);
				}
			} else {
				if (!dirtyFieldLocations.contains(fieldLocation)) {
					dirtyFieldLocations.add(fieldLocation);
				}
				if (fieldLocation.meta instanceof MetaGridCell) {
					MetaGridCell cell = (MetaGridCell) fieldLocation.meta;
					if (cell.isSelect()) {
						fieldLocation.getGridRow().setObject(this, SystemField.SELECT_FIELD_KEY, value);
					}
				}
			}
		}
		return result;
	}

//	// 是否是新增的表单，如果是新增的表单，需要把这个Document都传递给客户端，如果不是新增的就值传递部分的脏数据,有点时候在后台会创建新的document
//	private boolean _isNew = false;
//
//	public void setNew(boolean isNew) {
//		_isNew = isNew;
//	}

	/**
	 * 以json的形式返回脏数据
	 * @param context
	 *
	 * @return
	 * @throws Throwable
	 */
	public JSONObject getDirtyJSON(RichDocumentContext context) throws Throwable {
		JSONObject result = new JSONObject();
		result.put(DocumentJSONConstants.DOCUMENT_OID, getOID());
		result.put(DocumentJSONConstants.DOCUMENT_POID, getPOID());
		result.put(DocumentJSONConstants.DOCUMENT_VERID, getVERID());
		result.put(DocumentJSONConstants.DOCUMENT_DVERID, getDVERID());
		result.put(DocumentJSONConstants.DOCUMENT_STATE, getState());
		result.put(DocumentJSONConstants.DOCUMENT_TYPE, getDocumentType());

		if (_closeFlag) {
			result.put("closeFlag", true);
			return result;
		}
		Object[] actions = {"getDirtyJSON"};
		int action = Performance.startAction(actions);

		registerPreloadData();
		super.calcDelayFormula();
		Set<String> sortDirtyTables = Collections.emptySet();
		if (dirtyTables.size() > 0) {
			MetaForm metaForm = this.getMetaForm();
			IDLookup idlookup = IDLookup.getIDLookup(metaForm);
			sortDirtyTables = sortDirtyTables(dirtyTables, idlookup);
			addDirtyTableAffectItems(context, sortDirtyTables);
			super.calcDelayFormula();
		}
		if (!isBlankOrNull(_message)) {
			result.put("returnMessage", _message);
		}
		if (!isBlankOrNull(_needRebuildComp)) {
			result.put("needRebuildComp", _needRebuildComp);
		}
		processTreeRowData();
		preProcessMetaColumnInfoByMetaTable(metaForm);
		appendSelectFieldColumnKey(metaForm);
		if (isFullData()) {
			result.put("isFullData", true);
			JSONObject docJson = this.toJSON();
			result.put("documentJson", docJson);
			JSONObject uiAttrResult = new CalcUtil(context, this, context.getFormKey(), true, true, true, true).calcAll();
			result.put("UIAttrCache", uiAttrResult);
//		} else if (_isNew) {
//			result.put("isNewDoc", _isNew);
//			JSONObject docJson = this.toJSON();
//			result.put("documentJson", docJson);
		} else {
			MetaForm metaForm = this.getMetaForm();
			IDLookup idlookup = IDLookup.getIDLookup(metaForm);
			//会包含没有dataBinging的字段
			Set<FieldLocation<?>> sortDirtyFieldLocations = new HashSet<FieldLocation<?>>();
			if (dirtyFieldLocations.size() > 0) {
				JSONArray fieldValues = new JSONArray();
				sortDirtyFieldLocations = sortDirtyFieldLocations(this.dirtyFieldLocations, idlookup);
				for (FieldLocation<?> fieldLocation : sortDirtyFieldLocations) {
					String tableKey = fieldLocation.getTableKey();
					if (dirtyTables.contains(tableKey)) {
						continue;
					}
					JSONObject obj = new JSONObject();
					obj.put("key", fieldLocation.getKey());
					//TODO:这里改为用bookmark序列化并表示唯一，前台js也要相应的修改
					obj.put("bookmark", fieldLocation.getBookMark());

					Object resultValue = this.getValue(context, fieldLocation, false);
					resultValue = toJSONValue(fieldLocation.getKey(), resultValue);
					obj.put("value", resultValue);

					fieldValues.put(obj);
					getDictCodeNameDirtyValue(idlookup, fieldValues, fieldLocation);
				}
				result.put("dirtyFieldValues", fieldValues);
			}
			
			// 处理空白行的差异
			if (emptyGridRowDirtyValues.size() > 0) {
				JSONArray emptyGridRowDirtyValuesJsonArray = new JSONArray();
				for (Entry<String, List<FieldLocation<?>>> e : emptyGridRowDirtyValues.entrySet()) {
					String gridKey = e.getKey();
					List<FieldLocation<?>> emptyRowDirty = emptyGridRowDirtyValues.get(gridKey);
					if (emptyRowDirty.size() == 0) {
						continue;
					}
					JSONObject emptyRowDirtyJsonObject = new JSONObject();
					JSONArray fieldValueJsonArray = new JSONArray();

					listSort(emptyRowDirty);

					for (FieldLocation<?> fieldLocation : emptyRowDirty) {
//						if (dirtyTables.contains(tableKey)) {
//							continue;
//						}
						JSONObject obj = new JSONObject();
						obj.put("key", fieldLocation.getKey());
						Object resultValue = this.getValue(context, fieldLocation, false);
						resultValue = toJSONValue(fieldLocation.getKey(), resultValue);
						obj.put("value", resultValue);
						fieldValueJsonArray.put(obj);
					}
					emptyRowDirtyJsonObject.put("tableKey", idlookup.getTableKeyByGridKey(gridKey));
					emptyRowDirtyJsonObject.put("gridKey", gridKey);
					emptyRowDirtyJsonObject.put("emptyRowValues", fieldValueJsonArray);
					emptyGridRowDirtyValuesJsonArray.put(emptyRowDirtyJsonObject);
				}
				result.put("emptyGridRowDirtyValues", emptyGridRowDirtyValuesJsonArray);
			}
			if (dirtyTables.size() > 0) {
				JSONArray tables = new JSONArray();
				for (String tableKey : sortDirtyTables) {
					List<String> gridKeys = idlookup.getGridKeyListByTableKey(tableKey);
					String tableViewRowKey = idlookup.getTableViewRowKeyByTableKey(tableKey);
					if ((gridKeys == null || gridKeys.size() == 0) && isBlankOrNull(tableViewRowKey)) {
						JSONObject obj = new JSONObject();
						obj.put("isGrid", false);
						obj.put("tableKey", tableKey);
						obj.put("dataTable", get(tableKey).toJSON());
						pColOneTableFieldDirtyData(metaForm.getMetaTable(tableKey), obj);
						tables.put(obj);
					} else {
						List<String> controlKeys;
						if (gridKeys == null || gridKeys.size() == 0) {
							controlKeys = Collections.singletonList(tableViewRowKey);
						} else {
							controlKeys = gridKeys;
						}
						for (String controlKey : controlKeys) {
							JSONObject obj = new JSONObject();
							obj.put("isGrid", true);
							obj.put("gridKey", controlKey);
							obj.put("tableKey", tableKey);
							// 参照 com.bokesoft.yigo.struct.document.Document.toJSONImpl(boolean) 方法 补充缺失的属性
							// 测试用例：采购发票删除采购订单相关明细时，没有删除条件表的值
							// 权限处理，如果DataObject设置的是加载后权限检查
							// 在二开中新增行，或者通过查询SQL得到的DataTable赋值到document中，在这里进行权限检查
							MetaDataObject metaDataObject = this.getMetaDataObject();
							MetaTable metaTable = metaDataObject == null ? null : metaDataObject.getMetaTable(tableKey);
							DataTable dataTable = get(tableKey);
							if (metaTable != null && metaDataObject.isCheckAfterLoad()) {
								// TODO 后面有空再优化，目前权限通用方法没有仅仅对DataTable做检查的
								AuthorityCheckUtil.checkAfterLoad(this.getContext(), this.getContext().getDocument());
							}
							JSONObject tableJSON = dataTable.toJSON();
							if (metaTable != null) {
								tableJSON.put("isT", metaTable.isT());
								tableJSON.put("parentKey", metaTable.getParentKey());
								tableJSON.put("tableMode", metaTable.getTableMode());
								tableJSON.put("isPersist", metaTable.isPersist());
							}
							obj.put("dataTable", tableJSON);
							appendSingleVirtualGridJSON(tableKey, tableJSON);
							tables.put(obj);
						}
					}
				}
				result.put("dirtyDataTables", tables);
			}
			
			// 字典caption批量计算
			this.setDictionaryCaption(context, metaForm);
			
//			this.preCalcComboBoxNoCacheFormulaValue(context);
			
			this.getExpandDataDirtyJSON(context, metaForm, result);

			JSONObject uiAttrResult = super.calcDelayUIFormula(context, sortDirtyFieldLocations, sortDirtyTables, emptyGridRowDirtyValues);
			result.put("UIAttrCache", uiAttrResult);
		}

		//收集form的paras,放到这里是由于上面默认值计算过程中有可能修改form的paras中参数。放到RichDocumentContext中处理参数会导致参数丢失
		Paras paras = context.getParas();
		if (paras != null) {
			this.appendUICommand(new UICommand(UICommand.UI_CMD_UpdateFormParas, paras.toJSON()));
		}
		DefaultContext parentContext = context.getParentContext();
		if (parentContext != null) {
			paras = parentContext.getParas();
			if (paras != null) {
				this.appendUICommand(new UICommand(UICommand.UI_CMD_UpdateParentFormParas, paras.toJSON()));
			}
		}

		StringHashMap<String> operatorParas = context.getOperatorParas();
		if (operatorParas != null) {
			JSONObject obj = new JSONObject();
			for (Entry<String, String> entry : operatorParas.entrySet()) {
				obj.put(entry.getKey(), entry.getValue());
			}
			this.appendUICommand(new UICommand(UICommand.UI_CMD_UpdateOperatorParas, obj));
		}

		if (uiCommands.size() > 0) {
			JSONArray json = new JSONArray();
			for (UICommand uiCommand : uiCommands) {
				JSONObject obj = new JSONObject();
				obj.put("key", uiCommand.key);
				obj.put("content", uiCommand.content);
				obj.put("args", uiCommand.args);
				json.put(obj);
			}
			result.put("uiCommands", json);
		}
//		List<MessageData> contextMessage = MessageContext.getFinalMessageData();
//		if (contextMessage.size() > 0) {
//			MessageData instance = new MessageData();
//			instance.toJSONArray(contextMessage);
//			result.put("messageInfo", instance.toJSONArray(contextMessage));
//		}
		Performance.endActive(action, actions);
		return result;
	}

	private void addDirtyTableAffectItems(RichDocumentContext context, Set<String> sortDirtyTables) throws Throwable {
		if (sortDirtyTables == null || sortDirtyTables.size() <= 0) {
			return;
		}
		final MetaForm metaForm = this.getMetaForm();
		IDLookup idlookup = IDLookup.getIDLookup(metaForm);
		for (String tableKey : sortDirtyTables) {
			List<String> gridKeys = idlookup.getGridKeyListByTableKey(tableKey);
			String tableViewRowKey = idlookup.getTableViewRowKeyByTableKey(tableKey);
			if ((gridKeys == null || gridKeys.size() == 0) && isBlankOrNull(tableViewRowKey)) {
				continue;
			} else {
				List<String> controlKeys;
				if (gridKeys == null || gridKeys.size() == 0) {
					controlKeys = Collections.singletonList(tableViewRowKey);
				} else {
					controlKeys = gridKeys;
				}
				for (String controlKey : controlKeys) {
					 rowCountChanged(context, controlKey, true);
				}
			}
		}
	}
	private void appendSelectFieldColumnKey(MetaForm metaForm) throws Throwable {
		final MetaDataSource dataSource = metaForm.getDataSource();
		final MetaDataObject dataObject = dataSource == null ? null : dataSource.getDataObject();
		if (dataObject == null) {
			return;
		}
		final IDLookup idLookup = IDLookup.getIDLookup(metaForm);
		final List<MetaGrid> metaGrids = idLookup.getMetaGrids();
		if (metaGrids == null || metaGrids.size() == 0) {
			return;
		}
		Map<String, String> tableKeys = new HashMap<>();
		for (MetaGrid grid : metaGrids) {
			final MetaGridRow detailMetaRow = grid.getDetailMetaRow();
			if (detailMetaRow == null) {
				continue;
			}
			final int selectIndex = grid.getSelectIndex();
			if (selectIndex < 0) {
				continue;
			}
			final MetaGridCell gridCell = detailMetaRow.get(selectIndex);
			final String tableKey = gridCell.getTableKey();
			final String columnKey = gridCell.getColumnKey();
			if (tableKeys.containsKey(tableKeys)) {
				continue;
			}
			final DataTable dataTable = this.get_impl(tableKey);
			if (dataTable == null) {
				tableKeys.put(tableKey, tableKey);
				continue;
			}
			final DataTableMetaData metaData = dataTable.getMetaData();
			final int columnIndex = metaData.findColumnIndexByKey(columnKey);
			if (columnIndex < 0) {
				final MetaTable table = dataObject.getTable(tableKey);
				final MetaColumn metaColumn = table.get(columnKey);
				ColumnInfo info = new ColumnInfo(metaColumn.getKey(), metaColumn.getDataType());
				dataTable.addColumn(info);
				tableKeys.put(tableKey, tableKey);
			}
		}
	}

	private void getDictCodeNameDirtyValue(IDLookup idLookup, JSONArray fieldValues, FieldLocation<?> fieldLocation) throws Throwable {
		int componentType = fieldLocation.getComponentType();
		if (componentType == ControlType.DICT || componentType == ControlType.COMPDICT || componentType == ControlType.DYNAMICDICT) {
			String tableKey = fieldLocation.getTableKey();
			String columnKey = fieldLocation.getColumnKey();
			if (StringUtil.isBlankOrNull(tableKey) || StringUtil.isBlankOrNull(columnKey)) {
				return;
			}
			DataTable dataTable = this.getDataTable(tableKey);
			if (dataTable == null || dataTable.size() == 0) {
				return;
			}
			MetaTable table = this.getMetaDataObject().getTable(tableKey);
			MetaColumn column = table.get(columnKey);
			String codeColumnKey = column.getCodeColumnKey();
			int rowIndex = dataTable.getRowIndexByBookmark(fieldLocation.getBookMark());
			if(fieldLocation instanceof HeadFieldLocation) {
				rowIndex = 0;
			}
			if (codeColumnKey.length() != 0) {
				String codeValue = dataTable.getString(rowIndex, codeColumnKey);
				JSONObject obj = new JSONObject();
				obj.put("tableKey", tableKey);
				obj.put("columnKey", codeColumnKey);
				obj.put("bookmark", dataTable.getBookmark(rowIndex));
				obj.put("value", codeValue);
				fieldValues.put(obj);
			}
		}
	}

	/**
	 * 将控件类型为动态字典的脏数据字段放到最后赋值，否则界面控件显示不正确
	 * 
	 * @param dirtyFieldLocations
	 * @param idLookup
	 * @return
	 * @throws Throwable
	 */
	private Set<FieldLocation<?>> sortDirtyFieldLocations(Set<FieldLocation<?>> dirtyFieldLocations, IDLookup idLookup)
			throws Throwable {
		if (dirtyFieldLocations == null || dirtyFieldLocations.size() <= 1) {
			return dirtyFieldLocations;
		}

		LinkedHashSet<FieldLocation<?>> result = new LinkedHashSet<FieldLocation<?>>();
		LinkedHashSet<FieldLocation<?>> dynamicDictDirtyFieldLocations = new LinkedHashSet<FieldLocation<?>>();
		LinkedHashSet<FieldLocation<?>> normalDirtyFieldLocations = new LinkedHashSet<FieldLocation<?>>();
		while (!dirtyFieldLocations.isEmpty()) {
			FieldLocation<?> fieldLocation = dirtyFieldLocations.iterator().next();
			String key = fieldLocation.getKey();
			MetaComponent component = idLookup.getComponentByKey(key);
			int controlType;
			if (component != null) {
				controlType = component.getControlType();
			} else {
				MetaGridCell gridCell = idLookup.getGridCellByKey(key);
				controlType = gridCell.getCellType();
			}

			if (controlType != ControlType.DYNAMICDICT) {
				normalDirtyFieldLocations.add(fieldLocation);
			} else {
				dynamicDictDirtyFieldLocations.add(fieldLocation);
			}
			dirtyFieldLocations.remove(fieldLocation);
		}

		result.addAll(normalDirtyFieldLocations);
		result.addAll(dynamicDictDirtyFieldLocations);
		return result;
	}

	/**
	 * 排序脏数据表,否则客户端加载表格数据时不正确
	 * 
	 * @param dirtyTables
	 */
	private Set<String> sortDirtyTables(Set<String> dirtyTables, IDLookup idLookup) {
		if (dirtyTables == null || dirtyTables.size() <= 1) {
			return dirtyTables;
		}
		LinkedHashSet<String> tableKeys = new LinkedHashSet<String>();
		MetaDataObject metaDataObject = this.getMetaDataObject();
		if (metaDataObject != null) {
			Set<String> currentLevelTableKeys = new HashSet<String>();
			MetaTableCollection tableCollection = metaDataObject.getTableCollection();
			if (tableCollection == null || tableCollection.size() <= 0) {
				return tableKeys;
			}
			
			// 这里写2个for循环是为了排序
			for (MetaTable table : tableCollection) {
				if (table.getTableMode() != TableMode.DETAIL) {
					//FIXME: 这里是否符合配置要求，有待商榷
					currentLevelTableKeys.add(table.getKey());
					if (dirtyTables.contains(table.getKey())) {
						tableKeys.add(table.getKey());
					}
				}
			}
			for (MetaTable table : tableCollection) {
				if (table.getTableMode() == TableMode.DETAIL && StringUtil.isBlankOrNull(table.getParentKey())) {
					currentLevelTableKeys.add(table.getKey());
					if (dirtyTables.contains(table.getKey())) {
						tableKeys.add(table.getKey());
					}
				}
			}
			addSubTableKeys(tableKeys, currentLevelTableKeys, dirtyTables, idLookup);
		}

		return tableKeys;
	}

	private void addSubTableKeys(LinkedHashSet<String> tableKeys, Set<String> parentTableKeys, Set<String> dirtyTables, IDLookup idLookup) {
		Set<String> currentLevelTableKeys = new HashSet<String>();
		for (String tableKey : parentTableKeys) {
			List<String> childTableKeys = idLookup.getChildTableKeys(tableKey);
			if (childTableKeys == null) {
				continue;
			}
			for (String childTableKey : childTableKeys) {
				currentLevelTableKeys.add(childTableKey);
				if (dirtyTables.contains(childTableKey)) {
					tableKeys.add(childTableKey);
				}
			}
		}

		if (dirtyTables.size() != tableKeys.size()) {
			addSubTableKeys(tableKeys, currentLevelTableKeys, dirtyTables, idLookup);
		}
	}

	private void preProcessMetaColumnInfoByMetaTable(MetaForm metaForm) {
		if (metaForm == null) {
			return;
		}
		MetaDataSource dataSource = metaForm.getDataSource();
		if (dataSource == null) {
			return;
		}
		MetaDataObject dataObject = dataSource.getDataObject();
		if (dataObject == null) {
			return;
		}

		MetaTableCollection tableCollection = dataObject.getTableCollection();
		if (tableCollection == null) {
			return;
		}
		for (MetaTable metaTable : tableCollection) {
			preProcessMetaColumnInfoByMetaTable(metaForm, metaTable.getKey());
		}
	}
	private void preProcessMetaColumnInfoByMetaTable(MetaForm metaForm, String tableKey) {
		MetaTable metaTable = metaForm.getMetaTable(tableKey);
		if (metaTable == null) {
			return;
		}
		DataTable table = get(tableKey);
		if (table == null) {
			return;
		}
		DataTableMetaData metaData = table.getMetaData();
		Iterator<MetaColumn> it = metaTable.iterator();
		MetaColumn metaColumn = null;
		while (it.hasNext()) {
			metaColumn = it.next();
			if (metaColumn.isHidden()) {
                continue;
            }
			ColumnInfo column = null;
			if (metaData.constains(metaColumn.getBindingDBColumnName())) {
				column = metaData.getColumnInfo(metaColumn.getBindingDBColumnName());
			}
			if (metaData.constains(metaColumn.getKey())) {
				column = metaData.getColumnInfo(metaColumn.getKey());
			}
			if (column == null) {
				continue;
			}
			column.setAccessControl(metaColumn.isAccessControl());
			column.setPrimary(metaColumn.getIsPrimary());
			column.setScale(metaColumn.getScale());
			if (metaColumn.getDataType() == DataType.STRING) {
				column.setLength(metaColumn.getLength());
			}
		}
	}

	/**
	 * 处理树形结构数据
	 * 
	 * @throws Throwable
	 */
	private void processTreeRowData() throws Throwable {
		MetaForm metaForm = getMetaForm();
		MetaDataSource dataSource = metaForm.getDataSource();
		
		if (dataSource == null) {
			return;
		}
		MetaDataObject dataObject = dataSource.getDataObject();
		if (dataObject == null) {
			return;
		}
		
		IDLookup idLookup = IDLookup.getIDLookup(metaForm);
		List<MetaGrid> metaGrids = idLookup.getMetaGrids();
		if (metaGrids == null) {
			return;
		}
		
		for (MetaGrid metaGrid : metaGrids) {
			String tableKey = idLookup.getTableKeyByGridKey(metaGrid.getKey());
			DataTable table = getDataTable(tableKey);
			if (table == null) {
				continue;
			}
			
			DataTableMetaData metaData = table.getMetaData();
			if (!metaData.constains(DataConstant.STR_FLD_TREEROWLEVEL)) {
				continue;
			}
			
			boolean hasColumnExpand = metaGrid.hasColumnExpand();

			// if (hasColumnExpand) {
			// if (!metaData.constains(DataConstant.STR_FLD_PARENT_TREEROWLEVEL))
			// {
			// continue;
			// }
			// } else {
			if (!metaData.constains(DataConstant.STR_FLD_TREE_ROWINDEX)) {
				continue;
			}
			if (!metaData.constains(DataConstant.STR_FLD_PARENT_TREE_ROWINDEX)) {
				continue;
			}
			// }
			MetaGridRowCollection rowCollection = metaGrid.getRowCollection();
			if (rowCollection == null) {
				continue;
			}
			MetaTable metaTable = dataObject.getMetaTable(tableKey);
			MetaColumn metaColumn = metaTable.get(DataConstant.STR_FLD_TREEROWLEVEL);
			for (MetaGridRow metaGridRow : rowCollection) {
				MetaRowTree rowTree = metaGridRow.getRowTree();
				if (rowTree == null) {
					continue;
				}
				if (rowTree.getTreeType() != GridTreeType.COMMON) {
					continue;
				}
				
				String parent = rowTree.getParent();
				String foreign = rowTree.getForeign();

				int pos = table.getPos();
				try {
					HashMap<String, Integer> groupLevels = new HashMap<String, Integer>();
					int preLevel =0 ;
					String key;
					String parentKey;
					for (int rowIndex = 0; rowIndex < table.size(); rowIndex++) {
						table.setPos(rowIndex);
						int level = TypeConvertor
								.toInteger(table.getObject(rowIndex, metaColumn.getBindingDBColumnName()));
						if (hasColumnExpand) {
							
							key = level + "";
							if (preLevel != level) {
								// 不同层级使用当前行。
								groupLevels.put(key, rowIndex + 1);
							} else if (rowIndex > 0) {
								// 相同层级看分组字段是否一致。一致的使用该层级第一个行。
								String primaryValues = getPrimaryValues(metaTable, table, rowIndex);
								String prePrimaryValues = getPrimaryValues(metaTable, table, rowIndex - 1);
								if (!prePrimaryValues.equalsIgnoreCase(primaryValues)) {
									groupLevels.put(key, rowIndex + 1);
								}
							}
							parentKey = (level - 1) + "";
							int parentRowIndex = 0;
							if (groupLevels.containsKey(parentKey)) {
								parentRowIndex = groupLevels.get(parentKey);
							}
							
							preLevel = level;
							table.setObject(rowIndex, parent, groupLevels.get(key));
							table.setObject(rowIndex, foreign, parentRowIndex);
						} else {
							key = level + "";
							groupLevels.put(level + "", rowIndex);
							int parentRowIndex = 0;
							if (rowIndex != 0 && level > 1 && groupLevels.get((level - 1) + "") != null) {
								key = (level - 1) + "";
								parentRowIndex = groupLevels.get(key) + 1;
							}
							table.setObject(rowIndex, parent, rowIndex + 1);
							table.setObject(rowIndex, foreign, parentRowIndex);
						}
						table.setState(table.getState(rowIndex));
					}
				} finally {
					table.setPos(pos);
				}
			}
		}
	}

	private String getPrimaryValues(MetaTable metaTable, DataTable table, int rowIndex) {
		StringBuffer sb = new StringBuffer();
		DataTableMetaData metaData = table.getMetaData();
		Iterator<MetaColumn> it = metaTable.iterator();
		MetaColumn metaColumn = null;
		while (it.hasNext()) {
			metaColumn = it.next();
			if (!metaColumn.getIsPrimary()) {
                continue;
            }
			ColumnInfo column = null;
			if (metaData.constains(metaColumn.getBindingDBColumnName())) {
				column = metaData.getColumnInfo(metaColumn.getBindingDBColumnName());
			}
			if (metaData.constains(metaColumn.getKey())) {
				column = metaData.getColumnInfo(metaColumn.getKey());
			}
			if (column == null) {
				continue;
			}
			String value = TypeConvertor.toString(table.getObject(rowIndex, column.getColumnKey()));
			sb.append("|").append(value);
		}
		return sb.toString();
	}

	private void listSort(List<FieldLocation<?>> emptyRowDirty) {
		Collections.sort(emptyRowDirty, new Comparator<FieldLocation<?>>() {
			@Override
			public int compare(FieldLocation<?> o1, FieldLocation<?> o2) {
				try {
					if (o1.getKey().endsWith("ItemKey") && !o2.getKey().endsWith("ItemKey")) {
						return -1;
					}
					if (!o1.getKey().endsWith("ItemKey") && o2.getKey().endsWith("ItemKey")) {
						return 1;
					}
					return 0;
				} catch (Exception e) {
					LogSvr.getInstance().error(e.getMessage(), e);
				}
				return 0;
			}
			
		});
	}

	private void pColOneTableFieldDirtyData(MetaTable metaTable, JSONObject result) throws Exception {
		IDLookup idlookup = IDLookup.getIDLookup(this.getMetaForm());
		JSONArray fieldValues = new JSONArray();
		DataTable dataTable = get(metaTable.getKey());
		Long oid = new Long(0);
		if (dataTable.size() > 0 && metaTable.getTableMode() == TableMode.DETAIL) {
			oid = dataTable.getLong(0, SystemField.OID_SYS_KEY);
		}
		String tableKey = metaTable.getKey();
		Map<String, List<String>> columKeysAndFieldKeys = idlookup.getColumKeysAndFieldListKeys(tableKey);
		if (columKeysAndFieldKeys == null) { // 设计器在采购订单源代码界面按住“Backspace”键不放，当tableKey为MM_PurchaseOrdergrid51_NODB（grid5为grid的key，1为rowType）时，有时会跑到这里
			return;
		}
		for (Map.Entry<String, List<String>> entry : columKeysAndFieldKeys.entrySet()) {
			List<String> fieldKeys = entry.getValue();
			for (String fieldKey : fieldKeys) {
				String columnKey = entry.getKey();
				JSONObject obj = new JSONObject();
				obj.put("key", fieldKey);
				obj.put("oid", oid);
				Object value = null;
				if (dataTable.size() > 0) {
					value = dataTable.getObject(0, columnKey);
				} else {
					continue;
					// 设计器在采购订单源代码界面按住“Backspace”键不放，当tableKey为MM_PurchaseOrdergrid51_NODB（grid5为grid的key，1为rowType）时，有时会跑到这里
					//throw new Exception("不应该出现这样的情况");
				}
				value = toJSONValue(fieldKey, value);
				obj.put("value", value);
				fieldValues.put(obj);
			}
		}

		result.put("oneTableFieldDirtyData", fieldValues);
	}
	

	@Override
	public void clear() {
		super.clear();

		uiCommands.clear();

		dirtyFieldLocations.clear();

		for (List<FieldLocation<?>> tmp : emptyGridRowDirtyValues.values()) {
			tmp.clear();
			tmp = null;
		}
		emptyGridRowDirtyValues.clear();

		dirtyTables.clear();
	}

	/**
	 * 清除脏数据等
	 */
	public void clearDirtyData() {
		_closeFlag = false;
		_message = "";
		_needRebuildComp = "";
		uiCommands.clear();
		dirtyFieldLocations.clear();
		for (List<FieldLocation<?>> tmp : emptyGridRowDirtyValues.values()) {
			tmp.clear();
			tmp = null;
		}
		emptyGridRowDirtyValues.clear();
		dirtyTables.clear();
		setFullData(false);
	}
	
	
}
