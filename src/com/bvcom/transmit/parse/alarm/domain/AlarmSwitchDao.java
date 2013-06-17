package com.bvcom.transmit.parse.alarm.domain;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.bvcom.transmit.db.DaoSupport;
import com.bvcom.transmit.util.AlarmSwitchMemory;
import com.bvcom.transmit.util.DaoException;

public class AlarmSwitchDao {
	private static Logger log = Logger.getLogger(AlarmSwitchDao.class
			.getCanonicalName());

	/**
	 * 
	 * @return alarmSwitchList��������״���� JI LONG 2011-5-12
	 */
	public List<AlarmSwitch> list() {
		List<AlarmSwitch> alarmSwitchList = new ArrayList<AlarmSwitch>();

		Statement statement = null;
		ResultSet rs = null;
		Connection conn = null;
		// freq,serviceid,switchtype,switchvalue,alarmtype
		try {
			conn = DaoSupport.getJDBCConnection();
			statement = conn.createStatement();

			String sqlStr = "SELECT * FROM alarmswitch a;";
			rs = statement.executeQuery(sqlStr);
			while (rs.next()) {
				AlarmSwitch alarmSwitch = new AlarmSwitch();
				alarmSwitch = new AlarmSwitch();
				alarmSwitch.setFreq(rs.getString("freq"));
				alarmSwitch.setServiceID(rs.getString("serviceid"));
				alarmSwitch.setSwitchType(rs.getInt("switchtype"));
				alarmSwitch.setSwitchValue(rs.getInt("switchvalue"));
				alarmSwitch.setAlarmType(rs.getInt("alarmtype"));
				alarmSwitchList.add(alarmSwitch);
			}
		} catch (Exception e) {
			log.error("���ұ�������״̬���ϴ���: " + e.getMessage());
		} finally {
			try {
				DaoSupport.close(rs);
				DaoSupport.close(statement);
				DaoSupport.close(conn);
			} catch (DaoException e) {
				log.error("�ر����ݿ�ʧ��: " + e.getMessage());
			}
		}
		return alarmSwitchList;
	}

	/**
	 * 
	 * @param alarmSwitchList
	 *            ��Ҫ����ı������ؼ��� ����ʱ�����ж�����Ѿ�������£�û������� JI LONG 2011-5-12
	 */
	public void save(List<AlarmSwitch> alarmSwitchList) {
		Statement statement = null;
		Connection conn = null;
		// ���ݿ��еĿ���״̬
		List<AlarmSwitch> list =AlarmSwitchMemory.alarmSwitchList;
		try {
			conn = DaoSupport.getJDBCConnection();
			conn.setAutoCommit(false);
			// freq,serviceid,switchtype,switchvalue,alarmtype
			String sqlStr = "";
			for (int i = 0; i < alarmSwitchList.size(); i++) {
				AlarmSwitch as=alarmSwitchList.get(i);
				//�����������Ϊ��Ŀ��
				if(as.getSwitchType()==1){
					//���freq��serviceid��Ϊarr
					if(as.getFreq().equals("arr")&&as.getServiceID().equals("arr")){
						delete(as.getSwitchType(),as.getFreq());
					//���freq������arr servicidΪarr	
					}else if(as.getServiceID().equals("arr")&&(!as.getFreq().equals("arr"))){
						delete(as.getSwitchType(),as.getFreq());
					}
				//�����������ΪƵ�ʼ� ����Ƶ��Ϊarrʱ	
				}else{
					if(as.getFreq().equals("arr")){
						delete(as.getSwitchType(),"");
					}
				}
				AlarmSwitchMemory.alarmSwitchList=list();
				if(elementIsList(list,as)){
					sqlStr="update alarmswitch set switchvalue = "+as.getSwitchValue()+" where " +
					"freq = '"+as.getFreq()+"' and serviceid = '"+as.getServiceID()+"' and " +
					"switchtype = "+as.getSwitchType()+" and alarmtype = "+as.getAlarmType()+";" ;
				}else{
					sqlStr="insert into alarmswitch (freq,serviceid,switchtype,switchvalue,alarmtype)" +
					"values('"+as.getFreq()+"','"+as.getServiceID()+"',"+as.getSwitchType()+"," +
					as.getSwitchValue()+","+as.getAlarmType()+");";
				}
				try {
					statement = conn.createStatement();
					statement.executeUpdate(sqlStr);

				} catch (Exception e) {
					log.error("����������Ϣ�������ݿ����: " + e.getMessage());
					log.error("����������Ϣ�������ݿ���� SQL��\n" + sqlStr);
				} finally {
					DaoSupport.close(statement);
				}
			}
			conn.commit();
		} catch (Exception e) {
			e.printStackTrace();
			log.error("������±�������״̬���ϴ���: " + e.getMessage());
			try {
				conn.rollback();
			} catch (SQLException e1) {
			}
		} finally {
			try {
				DaoSupport.close(conn);
			} catch (DaoException e) {
				log.error("�ر����ݿ�ʧ��: " + e.getMessage());
			}
		}
		AlarmSwitchMemory.alarmSwitchList=list();
//		log.info("���º�Ŀ���״̬"+AlarmSwitchMemory.alarmSwitchList);
	}

	/**
	 * �жϸı��������Ƿ��Ѿ�����
	 * @param alarmSwitchList
	 * @param alarmSwitch
	 * @return true����
	 */
	private boolean elementIsList(List<AlarmSwitch> alarmSwitchList,
			AlarmSwitch alarmSwitch) {
		boolean temp = false;
		for (AlarmSwitch as : alarmSwitchList) {
			if (as.getFreq().equals(alarmSwitch.getFreq())
					&& as.getServiceID().equals(alarmSwitch.getServiceID())
					&& as.getSwitchType() == alarmSwitch.getSwitchType()
					&& as.getAlarmType() == alarmSwitch.getAlarmType()) {
				temp=true;
				break;
			}
		}
		return temp;
	}
	/**
	 * switchValue 1��ʾ��Ŀ��  2��ʾƵ�ʼ�
	 * �������ΪƵ�ʼ� ���Ƶ�ʿ�����ص����м�¼ɾ��
	 * �������Ϊ��Ŀ�� ���� freqΪarr  ��ѽ�Ŀ��ص����м�¼ɾ��
	 * �������Ϊ��Ŀ�� ���� freq������arr  ��Ѷ�Ӧ freq�µ� ��Ŀ��ؿ��ؼ�¼ɾ�� 
	 * @param switchValue
	 * @param freq
	 */
	private void delete(int switchValue,String freq){
		Statement statement = null;
		Connection conn = null;
		String sqlStr = "";
		if(freq.equals("")&&switchValue==2){
			sqlStr="delete FROM alarmswitch where switchtype = 2;";
		}else if(freq.equals("arr")&&switchValue==1){
			sqlStr="delete FROM alarmswitch where switchtype = 1;";
		}else if((!freq.equals("arr"))&&(freq.equals(""))){
			sqlStr="delete FROM alarmswitch where switchtype = 1 and freq = '"+freq+"';";
		}
		try {
			conn = DaoSupport.getJDBCConnection();
			conn.setAutoCommit(false);
			try {
				statement = conn.createStatement();
				statement.execute(sqlStr);
			}  catch (Exception e) {
				log.error("ɾ������������Ϣ���ݿ����: " + e.getMessage());
				log.error("ɾ������������Ϣ���ݿ���� SQL��\n" + sqlStr);
			} finally {
				DaoSupport.close(statement);
			}
			conn.commit();
		} catch (Exception e) {
			e.printStackTrace();
			log.error("ɾ����������״̬����: " + e.getMessage());
			try {
				conn.rollback();
			} catch (SQLException e1) {
			}
		} finally {
			try {
				DaoSupport.close(conn);
			} catch (DaoException e) {
				log.error("�ر����ݿ�ʧ��: " + e.getMessage());
			}
		}
	}
}
