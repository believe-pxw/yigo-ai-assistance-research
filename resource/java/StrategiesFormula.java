package com.bokesoft.erp.pm.function;

import java.math.BigDecimal;
import java.util.List;

import com.bokesoft.erp.billentity.bk_basic.BK_Unit;
import com.bokesoft.erp.billentity.bk_basic.BK_UnitSystem;
import com.bokesoft.erp.billentity.pmconfig.message.MessageConstant;
import com.bokesoft.erp.billentity.pmconfig.EPM_PackageSequenceDtl;
import com.bokesoft.erp.billentity.pmconfig.EPM_StrategyDtl;
import com.bokesoft.erp.billentity.pmconfig.PM_PackageSequence;
import com.bokesoft.erp.billentity.pmconfig.PM_Strategy;
import com.bokesoft.erp.billentity.ppconfig.EPP_Routing;
import com.bokesoft.erp.billentity.ppconfig.EPP_Routing_MaintenancePack;
import com.bokesoft.erp.entity.util.EntityContextAction;
import com.bokesoft.erp.pp.PPConstant;
import com.bokesoft.yes.common.util.StringUtil;
import com.bokesoft.yes.erp.message.MessageFacade;
import com.bokesoft.yes.mid.cmd.richdocument.strut.RichDocument;
import com.bokesoft.yes.mid.cmd.richdocument.strut.RichDocumentContext;
import com.bokesoft.yes.mid.parameterizedsql.SqlString;
import com.bokesoft.yes.mid.parameterizedsql.SqlStringUtil;
import com.bokesoft.yigo.common.util.TypeConvertor;
import com.bokesoft.yigo.struct.datatable.DataTable;
/**
 * 维护策略相关的方法
 * (1)维护策略包上时刻更新最小计量单位到表头上的策略单位
 *
 */
public class StrategiesFormula extends EntityContextAction {

	public StrategiesFormula(RichDocumentContext _context) {
		super(_context);
	}

	/**
	 * 维护策略包上时刻更新最小计量单位到表头上的策略单位
	 * 
	 * @throws Throwable
	 */
	public void setMinUnitID() throws Throwable {
		PM_Strategy strategies = PM_Strategy.parseEntity(_context);
		BigDecimal min = BigDecimal.ZERO;
		Long minUnitID = 0L;
		for (EPM_StrategyDtl dtl : strategies.epm_strategyDtls()) {
			Long packageUnitID = dtl.getPackageUnitID();
			if (packageUnitID <= 0 || packageUnitID.equals(minUnitID)) {
                continue;
            }
			BK_Unit packageUnit = BK_Unit.load(_context, packageUnitID);
			long packageNumerator = packageUnit.getNumerator();
			long packageDenominator = packageUnit.getDenominator();
			BigDecimal cur = TypeConvertor.toBigDecimal(packageNumerator).divide(TypeConvertor.toBigDecimal(packageDenominator), 10, BigDecimal.ROUND_HALF_UP);
			if (minUnitID <= 0 || cur.compareTo(min) < 0) {
				min = cur;
				minUnitID = packageUnitID;
			}
		}

//		if (strategies.epm_strategyDtls() == null || strategies.epm_strategyDtls().size() == 0) {
////			evalFormula("SetEnable(" + EPM_Strategy.StrategyUnitID + ",true)", "");
//		} else {
////			evalFormula("SetEnable(" + EPM_Strategy.StrategyUnitID + ",false)", "");
//		}
		Long oldUnitID = strategies.getStrategyUnitID();
		strategies.setStrategyUnitID(minUnitID);
		if (minUnitID <= 0) {

			strategies.setStrategyUnitID(oldUnitID);
		}
	}

	/**
	 * 获取策略信息
	 * @param schedulingIndicator
	 * @return
	 * @throws Throwable
	 */
	public SqlString getStrategyUnitIDbyIndicator(int schedulingIndicator) throws Throwable {
		SqlString sqlString = new SqlString();
		StringBuilder sb = new StringBuilder();

		List<BK_UnitSystem> list = BK_UnitSystem.loader(_context).UnitTime(1).loadList();
		Long[] longs = new Long[list.size()];
        for (int i = 0; i < list.size(); i++) {
            longs[i] = list.get(i).getOID();
        }
		// 时间类型只过滤出时间单位
		if (schedulingIndicator < 3) {
			List<BK_Unit> unitList = BK_Unit.loader(_context).UnitSystemID(longs).loadList();
			if (unitList != null && unitList.size() > 0) {
				for (BK_Unit dtl : unitList) {
					if (sb.length() == 0) {
						sb.append(dtl.getOID());
					} else {
						sb.append(",");
						sb.append(dtl.getOID());
					}
				}
			}
		}

		if (schedulingIndicator == 3) {
			List<BK_Unit> unitList = BK_Unit.loader(_context).loadList();
			if (unitList != null && unitList.size() > 0) {
				for (BK_Unit dtl : unitList) {
					if (sb.length() == 0) {
						sb.append(dtl.getOID());
					} else {
						sb.append(",");
						sb.append(dtl.getOID());
					}
				}
			}
		}

		if (sb.length() == 0) {
			sb.append("0");
		}
		sqlString.append(SqlStringUtil.genMultiParameters(sb.toString()));
		return sqlString;
	}

	/**
	 * 根据表头最小单位以及计划标识过滤
	 * @param strategyUnitID 计量单位
	 * @param schedulingIndicator 标识
	 * @return
	 * @throws Throwable
	 */
	public SqlString getPackageUnitIDByStrategyUnitID(Long strategyUnitID, int schedulingIndicator) throws Throwable {
		SqlString sqlString = new SqlString();
		StringBuilder sb = new StringBuilder();
		List<BK_UnitSystem> list = BK_UnitSystem.loader(_context).UnitTime(1).loadList();
		Long[] longs = new Long[list.size()];
		for (int i = 0; i < list.size(); i++) {
			longs[i] = list.get(i).getOID();
		}
		// 时间类型只过滤出时间单位
		if (schedulingIndicator < 3) {
			List<BK_Unit> unitList = BK_Unit.loader(_context).UnitSystemID(longs).loadList();
			if (unitList != null && unitList.size() > 0) {
				for (BK_Unit dtl : unitList) {
					if (sb.length() == 0) {
						sb.append(dtl.getOID());
					} else {
						sb.append(",");
						sb.append(dtl.getOID());
					}
				}
			}
		}

		// 作业型过滤出表头单位所在单位组的所有单位
		if (schedulingIndicator == 3 && strategyUnitID > 0) {
			BK_Unit strategyUnit = BK_Unit.load(_context, strategyUnitID);
			List<BK_Unit> unitList = BK_Unit.loader(_context).UnitSystemID(strategyUnit.getUnitSystemID()).loadList();
			if (unitList != null && unitList.size() > 0) {
				for (BK_Unit dtl : unitList) {
					if (sb.length() == 0) {
						sb.append(dtl.getOID());
					} else {
						sb.append(",");
						sb.append(dtl.getOID());
					}
				}
			}
		}

		// 因为空值过滤掉了，理论上这段跑不到。。。
		if (schedulingIndicator == 3 && strategyUnitID <= 0) {
			List<BK_Unit> unitList = BK_Unit.loader(_context).loadList();
			if (unitList != null && unitList.size() > 0) {
				for (BK_Unit dtl : unitList) {
					if (sb.length() == 0) {
						sb.append(dtl.getOID());
					} else {
						sb.append(",");
						sb.append(dtl.getOID());
					}
				}
			}
		}

		if (sb.length() == 0) {
			sb.append("0");
		}
		sqlString.append(SqlStringUtil.genMultiParameters(sb.toString()));
		return sqlString;
	}

	/**
	 * 包顺序
	 * 
	 * @throws Throwable
	 */
	public void refreshPackageView() throws Throwable {
		PM_PackageSequence packageSequence = PM_PackageSequence.parseEntity(_context);

		Long strategiesID = packageSequence.getStrategyID();
		PM_Strategy strategies = PM_Strategy.load(_context, strategiesID);

		List<EPM_StrategyDtl> packageList = strategies.epm_strategyDtls();

		if (packageList == null || packageList.size() == 0) {
            return;
        }

		// 经过各种测试，发现显示规则如下
		// 先遍历每行得到最小单位
		// 每行转换成最小单位(除以转换率四舍五入取整，通过1mon与4week 以及2mon与4week的显示差异得到),
		// 通过隐藏字段“基本长度”来转换
		// 取转换后所有行最大公约数做最小正周期 (也需要将偏置纳入最大公约数考虑中)

		int gcd = TypeConvertor.toInteger(packageList.get(0).getBaseLength());

		for (EPM_StrategyDtl dtl : packageList) {
			int baseLength = TypeConvertor.toInteger(dtl.getBaseLength());
			int offset = dtl.getOffsetPos();
			gcd = gcd(gcd, baseLength);

			if (offset > 0) {
                gcd = gcd(gcd, offset);
            }
		}

		int count = packageSequence.getCount();

		if (packageSequence.epm_packageSequenceDtls() == null || packageSequence.epm_packageSequenceDtls().size() == 0) {
			for (EPM_StrategyDtl dtl : packageList) {
				EPM_PackageSequenceDtl newPackageSequenceDtl = packageSequence.newEPM_PackageSequenceDtl();
				newPackageSequenceDtl.setPackageNo(dtl.getPackageNo());
				newPackageSequenceDtl.setCycleNotes(dtl.getCycleNotes());
				newPackageSequenceDtl.setCycleHierarchy(dtl.getCycleHierarchy());
				int length = TypeConvertor.toInteger(dtl.getBaseLength()) / gcd;
				int offset = dtl.getOffsetPos() / gcd;
				for (int i = 15 * count + 1; i < 15 * (count + 1) + 1; i++) {
					if (offset > 0 && i == offset) {
						newPackageSequenceDtl.valueByFieldKey("DuePack" + i, dtl.getOffsetShortText());
					} else if ((i - offset) % length == 0 && i > offset) {
						newPackageSequenceDtl.valueByFieldKey("DuePack" + i, dtl.getCycleShortText());
					} else {
						newPackageSequenceDtl.valueByFieldKey("DuePack" + i, "");
					}
				}
			}
		} else // 输入向前日期，向后日期，不需要新增行，只需修改数据
		{
			for (EPM_StrategyDtl dtl : packageList) {
				List<EPM_PackageSequenceDtl> packageSequenceDtls = packageSequence.epm_packageSequenceDtls(EPM_PackageSequenceDtl.PackageNo, dtl.getPackageNo());
				if (packageSequenceDtls.size() == 0) {
					continue;
				}
				EPM_PackageSequenceDtl packageSequenceDtl = packageSequenceDtls.get(0);
				packageSequenceDtl.setPackageNo(dtl.getPackageNo());
				packageSequenceDtl.setCycleNotes(dtl.getCycleNotes());
				int length = TypeConvertor.toInteger(dtl.getBaseLength()) / gcd;
				int offset = dtl.getOffsetPos() / gcd;
				for (int i = 15 * count + 1; i < 15 * (count + 1) + 1; i++) {
					int flag = i % 15;
					if (flag == 0) {
                        flag = 15;
                    }
					if (offset > 0 && i == offset) {
						packageSequenceDtl.valueByFieldKey("DuePack" + flag, dtl.getOffsetShortText());
					} else if ((i - offset) % length == 0 && i > offset) {
						packageSequenceDtl.valueByFieldKey("DuePack" + flag, dtl.getCycleShortText());
					} else {
						packageSequenceDtl.valueByFieldKey("DuePack" + flag, "");
					}
				}
			}
		}

		// 处理层次
		for (int i = 1; i <= 15; i++) {
			int maxHierarchy = 0;
			for (EPM_PackageSequenceDtl dtl : packageSequence.epm_packageSequenceDtls()) {
				int currentHierarchy = dtl.getCycleHierarchy();
				if (!StringUtil.isBlankOrStrNull(TypeConvertor.toString(dtl.valueByFieldKey("DuePack" + i))) && currentHierarchy > maxHierarchy) {
					maxHierarchy = currentHierarchy;
				}
			}

			for (EPM_PackageSequenceDtl dtl : packageSequence.epm_packageSequenceDtls()) {
				int currentHierarchy = dtl.getCycleHierarchy();
				if (!StringUtil.isBlankOrStrNull(TypeConvertor.toString(dtl.valueByFieldKey("DuePack" + i))) && currentHierarchy < maxHierarchy) {
					dtl.valueByFieldKey("DuePack" + i, "");
				}
			}
		}

	}

	/**
	 * 辗转相除求最大公约数
	 * 
	 * @param x
	 * @param y
	 * @return
     */
	private int gcd(int x, int y) {
		int temp;
		while (x % y != 0) {
			temp = x % y;
			x = y;
			y = temp;
		}
		return y;
	}

	/**
	 * 策略中是否存在软件包
	 * 
	 * @return
	 * @throws Throwable
	 */
	public Boolean isExistStrategiesPackage() throws Throwable {
		DataTable rst = getDocument().getDataTable(EPM_StrategyDtl.EPM_StrategyDtl);
		if (rst == null || rst.size() == 0) {
			return false;
		}
		return true;
	}
	
	/**
	 * 0时间
	 */
	public static final  int SchedulingIndicator_1=1;
	
	/**
	 * 1 关键日期
	 */
	public static final  int SchedulingIndicator_2=2;
	
	/**
	 * 3 作业
	 */
	public static final  int SchedulingIndicator_3=3;

	/**
	 * 设置计划标识下拉框的值
	 * 
	 * @return
	 * @throws Throwable
	 */
	public String getSchedulingIndicatorComboListValue() throws Throwable {
		String initFormula = "0,时间;1,关键日期;2,工厂日历;3,作业";
		if (isExistStrategiesPackage()) {
			RichDocument doc = getDocument();
			int schedulingIndicator = TypeConvertor.toInteger(doc.getHeadFieldValue(PM_Strategy.SchedulingIndicator));
			if (schedulingIndicator == SchedulingIndicator_3) {
				initFormula = "3,作业";
			} else {
				initFormula = "0,时间;1,关键日期;2,工厂日历";
			}
		}
		return initFormula;
	}

	/**
	 * 检查周期长度的有效性
	 * 
	 * @param schedulingIndicator
	 * @param unitID
	 * @param value
	 * @param isCycleSet
	 * @return
	 * @throws Throwable
	 */
	public String checkCycleLength(int schedulingIndicator, Long unitID, int value, int isCycleSet) throws Throwable {
		if (value <= 0) {
			return MessageFacade.getMsgContent(MessageConstant._STRATEGIESFORMULA002);
		}
		if (unitID <= 0) {
			return "";
		}
		if (isCycleSet == 0 && schedulingIndicator != SchedulingIndicator_3) {
			BK_Unit unit = BK_Unit.load(_context, unitID);
			BK_Unit dUnit = BK_Unit.loader(_context).Code("d").loadFirst();

			long dLen = dUnit.getNumerator();
			long UnitLen = unit.getNumerator();

			if (((UnitLen * value) % dLen) != 0) {
                return MessageFacade.getMsgContent(MessageConstant._IP110);
            }
		}
		return "";
	}

	/**
	 * 检查偏置的有效性
	 * 
	 * @param schedulingIndicator
	 * @param unitID
	 * @param value
	 * @param offsetShortText
	 * @param isCycleSet
	 * @return
	 * @throws Throwable
	 */
	public String checkOffSet(int schedulingIndicator, Long unitID, int value, String offsetShortText, int isCycleSet) throws Throwable {
		if (!StringUtil.isBlankOrNull(offsetShortText)) {
			if (value < 0) {
				return MessageFacade.getMsgContent(MessageConstant._IP706);
			}
			if (value == 0) {
				return MessageFacade.getMsgContent(MessageConstant._IP805);
			}
			if (unitID <= 0) {
				return "";
			}
			if (isCycleSet == 0 && schedulingIndicator != SchedulingIndicator_3) {
				BK_Unit unit = BK_Unit.load(_context, unitID);
				BK_Unit dUnit = BK_Unit.loader(_context).Code("d").loadFirst();

				long dLen = dUnit.getNumerator();
				long UnitLen = unit.getNumerator();

				if (((UnitLen * value) % dLen) != 0) {
                    return MessageFacade.getMsgContent(MessageConstant._IP113);
                }
			}
		}
		return "";
	}

	/**
	 * 策略删除包检查是否已用在任务清单中
	 * 
	 * @param billDtlID
	 * @param isCycleSet
	 * @throws Throwable
	 */
	public void checkHasUsedStrategiesPackage(Long billDtlID, int isCycleSet) throws Throwable {
		if (billDtlID == 0) {
			return;
		}
		if (isCycleSet == 1) {// 周期集不检查
			return;
		}
		PM_Strategy strategies = PM_Strategy.parseEntity(_context);
		SqlString sql = new SqlString()
				.append("select * from ", EPP_Routing_MaintenancePack.EPP_Routing_MaintenancePack, " where ",
						EPP_Routing_MaintenancePack.IsRelation, "=").appendPara(1).append(" and  ", "PackageShortText = ")
				.appendPara(billDtlID);	
		DataTable rst = getResultSet(sql);
		if (rst != null && rst.size() > 0) {
			for (int rowIndex = 0; rowIndex < rst.size(); rowIndex++) {
				EPP_Routing routing = EPP_Routing.load(_context, rst.getLong(rowIndex, EPP_Routing_MaintenancePack.SOID));
				EPM_StrategyDtl strategiesDtl = strategies.epm_strategyDtl(billDtlID);
				if (routing.getRoutingListType().equalsIgnoreCase(PPConstant.TaskListType_E)) {
					MessageFacade.throwException(MessageConstant._IP818, strategies.getCode() + " " + strategiesDtl.getPackageNo());
				} else if (routing.getRoutingListType().equalsIgnoreCase(PPConstant.TaskListType_T)) {
					MessageFacade.throwException(MessageConstant._STRATEGIESFORMULA001, strategies.getCode() + " " + strategiesDtl.getPackageNo());
				} else {
					MessageFacade.throwException(MessageConstant._IP819, strategies.getCode() + " " + strategiesDtl.getPackageNo());
				}
			}
		}
	}
}
