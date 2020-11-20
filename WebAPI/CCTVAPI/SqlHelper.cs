using System;
using System.Collections.Generic;
using System.Text;
using System.Data.SqlClient;
using System.Data;
using System.Reflection;
using System.Collections;
using System.Configuration;

namespace CCTVAPI
{
    /// <summary>
    /// ���ݿ������
    /// </summary>
    public static class SqlHelper
    {
        #region ����
        public static int CommandTimeout = 360;
        /// <summary>
        /// ���ݿ����Ӷ���
        /// </summary>
        public static SqlConnection Con;
        /// <summary>
        /// ���򼯵�����
        /// </summary>
        ///private static string _assemblyName = "Entity";
        // ��web.config�����ļ�����������ַ���
        private static Hashtable connCache = Hashtable.Synchronized(new Hashtable());

         public static string Constr =  ConfigurationManager.ConnectionStrings["SQLConnStringHCIS1.1"]!=null? ConfigurationManager.ConnectionStrings["SQLConnStringHCIS1.1"].ConnectionString:"";
        
        public static string GetConnectionString(string connectionKey)
        {
            if (string.IsNullOrEmpty(connectionKey))
            {
                connectionKey= Constr;
            }
            if (connCache.ContainsKey(connectionKey))
            {
                return connCache[connectionKey].ToString();
            }
            else
            {
                string ks = connectionKey;
                connCache.Add(connectionKey, ks);
                return ks;
            }
        }
        #endregion

        #region ��ȡ���������ݿ����Ӷ���

        /// <summary>
        /// ��ȡ���������ݿ����Ӷ���
        /// </summary>
        [Obsolete("���Ż� Con �������Ƿ���Ҫ���ǡ�daniel.zhu ")]
        public static SqlConnection GetConn(string connectionString)
        {
            SqlConnection con = null;
            if (con == null)
            {
                con = new SqlConnection(GetConnectionString(connectionString));
                con.Open();
            }
           
            return con;
        }
        #endregion

        #region ����
        #region ����һ�����񲢿�ʼ
        [Obsolete("���Ż� ���ʹ��Con���� ��������������뵱ǰ�����޹���,��������ɡ��Ĵ��� daniel.zhu")]
        /// <summary> 
        /// ����һ�����񲢿�ʼ 
        /// </summary> 
        /// <returns>���ش�����</returns> 
        public static SqlTransaction BeginTransaction(string connectionString)
        {
            SqlHelper.Con = SqlHelper.GetConn(connectionString); //��仰��Զ����һ���µ����� ��ʲô���⡣
            SqlTransaction tran = SqlHelper.Con.BeginTransaction();
            return tran;
        }
        #endregion 
        
        #region  �ύ����
        /// <summary> 
        /// �ύ���� 
        /// </summary> 
        public static void CommitTransaction(SqlTransaction tranc)
        {
          
            tranc.Commit();
            SqlHelper.Con.Close();
        }
        #endregion 

        /// <summary>
        /// ִ�з���һ��һ�е����ݿ����
        /// </summary>
        /// <param name="commandText">SQL����洢������</param>
        /// <param name="commandType">SQL��������</param>
        /// <param name="param">SQL�����������</param>
        /// <returns>��һ�е�һ�еļ�¼</returns>
        public static int ExecuteScalar(string connectionString, string commandText, CommandType commandType, SqlTransaction tran, params SqlParameter[] param)
        {
            int count;
            using (SqlCommand cmd = new SqlCommand(commandText, tran.Connection))
            {
                try
                {
                    cmd.CommandType = commandType;
                    cmd.Parameters.AddRange(param);
                    cmd.Transaction = tran;
                    cmd.CommandTimeout = CommandTimeout;
                    if (cmd.Connection.State == ConnectionState.Closed)
                        SqlHelper.Con.Open();
                    count = Convert.ToInt32(cmd.ExecuteScalar());
                }
                catch (Exception ex)
                {
                    throw ex;
                }
            }
            return count;
        }

        /// <summary>
        /// ִ�з���һ��һ�е����ݿ����
        /// </summary>
        /// <param name="commandText">SQL����洢������</param>
        /// <param name="commandType">SQL��������</param>
        /// <param name="param">SQL�����������</param>
        /// <returns>��һ�е�һ�еļ�¼</returns>
        public static int ExecuteScalar(SqlConnection con, string commandText, CommandType commandType, SqlTransaction tran, params SqlParameter[] param)
        {
            int count;
            using (SqlCommand cmd = new SqlCommand(commandText, con))
            {
                try
                {
                    cmd.CommandType = commandType;
                    cmd.Parameters.AddRange(param);
                    cmd.Transaction = tran;
                    cmd.CommandTimeout = CommandTimeout;
                    if (con.State == ConnectionState.Closed)
                        con.Open();
                    count = Convert.ToInt32(cmd.ExecuteScalar());
                }
                catch (Exception ex)
                {
                    throw ex;
                }
            }
            return count;
        }

        /// <summary>
        /// ִ�з���һ��һ�е����ݿ����
        /// </summary>
        /// <param name="commandText">SQL����洢������</param>
        /// <param name="commandType">SQL��������</param>
        /// <param name="param">SQL�����������</param>
        /// <returns>��һ�е�һ�еļ�¼</returns>
        public static int ExecuteScalar(string connectionString, string commandText, CommandType commandType, params SqlParameter[] param)
        {
            int count;
            using (SqlConnection connection = SqlHelper.GetConn(connectionString))
            {
                if (connection.State == ConnectionState.Closed)
                    connection.Open();
                using (SqlCommand cmd = new SqlCommand(commandText, connection))
                {
                    try
                    {
                        cmd.CommandType = commandType;
                        cmd.Parameters.AddRange(param);
                        cmd.CommandTimeout = CommandTimeout;
                        count = Convert.ToInt32(cmd.ExecuteScalar());
                        connection.Close();
                    }
                    catch (Exception ex)
                    {
                        connection.Close();
                        throw ex;
                    }
                }
            }
            return count;
        }


        /// <summary>
        /// ִ�в���ѯ�����ݿ����
        /// </summary>
        /// <param name="commandText">SQL����洢������</param>
        /// <param name="commandType">SQL��������</param>
        /// <param name="param">SQL�����������</param>
        /// <returns>��Ӱ�������</returns>
        public static int ExecuteNonQuery(string connectionString, string commandText, CommandType commandType, SqlTransaction tran, params SqlParameter[] param)
        {

            int result;

            using (SqlCommand cmd = new SqlCommand(commandText, tran.Connection))
            {
                try
                {
                    cmd.CommandType = commandType;
                    cmd.Parameters.AddRange(param);
                    cmd.Transaction = tran;
                    cmd.CommandTimeout = CommandTimeout;
                    if (SqlHelper.Con.State == ConnectionState.Closed)
                        SqlHelper.Con.Open();
                    result = cmd.ExecuteNonQuery();
                }
                catch (Exception ex)
                {
                    throw ex;
                }
            }

            return result;
        }

        /// <summary>
        /// ִ�в���ѯ�����ݿ����
        /// </summary>
        /// <param name="commandText">SQL����洢������</param>
        /// <param name="commandType">SQL��������</param>
        /// <param name="param">SQL�����������</param>
        /// <returns>��Ӱ�������</returns>
        public static int ExecuteNonQuery(string connectionString, string commandText, CommandType commandType, params SqlParameter[] param)
        {

            int result;
            using (SqlConnection connection = SqlHelper.GetConn(connectionString))
            {
                if (connection.State == ConnectionState.Closed)
                    connection.Open();
                using (SqlCommand cmd = new SqlCommand(commandText, connection))
                {
                    try
                    {
                        cmd.CommandType = commandType;
                        cmd.Parameters.AddRange(param);
                        cmd.CommandTimeout = CommandTimeout;
                        result = cmd.ExecuteNonQuery();
                        connection.Close();
                    }
                    catch (Exception ex)
                    {
                        connection.Close();
                        throw ex;
                    }
                }
            }

            return result;
        }

        /// <summary>
        /// ִ�з���һ����¼�ķ��Ͷ���
        /// </summary>
        /// <typeparam name="T">��������</typeparam>
        /// <param name="reader">ֻ��ֻ������</param>
        /// <returns>���Ͷ���</returns>
        private static T ExecuteDataReader<T>(IDataReader reader)
        {
            T obj = default(T);
            try
            {
                Type type = typeof(T);
                obj = (T)Activator.CreateInstance(type);//�ӵ�ǰ��������ͨ������ķ�ʽ����ָ�����͵Ķ���
                //obj = (T)Assembly.Load(SqlHelper._assemblyName).CreateInstance(SqlHelper._assemblyName + "." + type.Name);//����һ����������ͨ������ķ�ʽ����ָ�����͵Ķ���
                PropertyInfo[] propertyInfos = type.GetProperties();//��ȡָ�������������������
                foreach (PropertyInfo propertyInfo in propertyInfos)
                {
                    for (int i = 0; i < reader.FieldCount; i++)
                    {
                        string fieldName = reader.GetName(i);
                        if (fieldName.ToLower() == propertyInfo.Name.ToLower())
                        {
                            object val = reader[propertyInfo.Name];//��ȡ����ĳһ����¼�����ĳһ����Ϣ
                            if (val != null && val != DBNull.Value)
                                propertyInfo.SetValue(obj, val, null);//�������ĳһ�����Ը�ֵ
                            break;
                        }
                    }
                }
            }
            catch (Exception ex)
            {
                throw ex;
            }
            return obj;
        }

        /// <summary>
        /// ִ�з���һ����¼�ķ��Ͷ���
        /// </summary>
        /// <typeparam name="T">��������</typeparam>
        /// <param name="commandText">SQL����洢������</param>
        /// <param name="commandType">SQL��������</param>
        /// <param name="param">SQL�����������</param>
        /// <returns>ʵ�����</returns>
        public static T ExecuteEntity<T>(string connectionString, string commandText, CommandType commandType, params SqlParameter[] param)
        {
            T obj = default(T);
           
            using (SqlHelper.Con)
            {
                using (SqlCommand cmd = new SqlCommand(commandText, SqlHelper.GetConn(connectionString)))
                {
                    cmd.CommandType = commandType;
                    cmd.Parameters.AddRange(param);
                    cmd.CommandTimeout = CommandTimeout;
                    SqlHelper.Con.Open();
                    SqlDataReader reader = cmd.ExecuteReader(CommandBehavior.CloseConnection);
                    while (reader.Read())
                    {
                        obj = SqlHelper.ExecuteDataReader<T>(reader);
                    }
                }
            }
            return obj;
        }

        /// <summary>
        /// ִ�з��ض�����¼�ķ��ͼ��϶���
        /// </summary>
        /// <typeparam name="T">��������</typeparam>
        /// <param name="commandText">SQL����洢������</param>
        /// <param name="commandType">SQL��������</param>
        /// <param name="param">SQL�����������</param>
        /// <returns>���ͼ��϶���</returns>
        public static List<T> ExecuteList<T>(string connectionString, string commandText, CommandType commandType, params SqlParameter[] param)
        {
            List<T> list = new List<T>();
           
            using (SqlHelper.Con)
            {
                using (SqlCommand cmd = new SqlCommand(commandText, SqlHelper.GetConn(connectionString)))
                {
                    try
                    {
                        cmd.CommandType = commandType;
                        cmd.Parameters.AddRange(param);
                        cmd.CommandTimeout = CommandTimeout;
                        SqlHelper.Con.Open();
                        SqlDataReader reader = cmd.ExecuteReader(CommandBehavior.CloseConnection);
                        while (reader.Read())
                        {
                            T obj = SqlHelper.ExecuteDataReader<T>(reader);
                            list.Add(obj);
                        }
                    }
                    catch (Exception ex)
                    {
                        throw ex;
                    }
                }
            }
            return list;
        }
        /// <summary>
        /// ����Ҫִ�еķǲ�ѯSQL��伯��
        /// </summary>
        /// <param name="sqlList"></param>
        public static bool TransactionExec(string connectionString,List<string> sqlList)
        {
            bool result = false;
           
            using (SqlConnection conn =  SqlHelper.GetConn(connectionString))
            {
                
                SqlCommand cmd = conn.CreateCommand();
                SqlTransaction tran = conn.BeginTransaction("transaction");
                cmd.Connection = conn;
                cmd.Transaction = tran;
                cmd.CommandTimeout = CommandTimeout;
                try
                {
                    foreach (string sql in sqlList)
                    {
                        cmd.CommandText = sql;
                        int rs = cmd.ExecuteNonQuery();
                        if (rs <= 0)
                        {
                            throw new Exception();
                        }
                    }
                    tran.Commit();
                    result = true;
                }
                catch (Exception ex)
                {
                    tran.Rollback();
                }
                return result;
            }
        }

        public static SqlDataReader GetSqlDataReader(string connectionString, string commandText, CommandType commandType, params SqlParameter[] param)
        {
           
            using (SqlHelper.Con)
            {
                using (SqlCommand cmd = new SqlCommand(commandText, SqlHelper.Con))
                {
                    try
                    {
                        cmd.CommandType = commandType;
                        cmd.Parameters.AddRange(param);
                        cmd.CommandTimeout = CommandTimeout;
                        SqlHelper.Con.Open();
                        return cmd.ExecuteReader(CommandBehavior.CloseConnection);
                    }
                    catch (Exception ex)
                    {
                        throw ex;
                    }
                }
            }
        }

        #endregion

        #region ExecuteDataSet(ִ�в�ѯ��䣬����DataSet)

        /// <summary>   ִ�в�ѯ��䣬����DataSet
        /// </summary>
        /// <param name="connectionString">�����ַ���</param>
        /// <param name="cmdText">ִ�����</param>
        /// <param name="cmdType">ִ�з�ʽ</param>
        /// <param name="commandParameters">��������</param>
        /// <returns>���ݼ�</returns>
        public static DataSet ExecuteDataSet(string connectionString, string cmdText, CommandType cmdType, SqlParameter[] commandParameters)
        {
            if (cmdText != null && cmdText.Trim() != "")
            {
                using (SqlConnection connection = SqlHelper.GetConn(connectionString))
                {
                    DataSet ds = new DataSet();
                    try
                    {
                        if(connection.State== ConnectionState.Closed)
                        connection.Open();

                        SqlDataAdapter command = new SqlDataAdapter();
                        command.SelectCommand = new SqlCommand();
                        command.SelectCommand.CommandTimeout = CommandTimeout;
                        command.SelectCommand.Connection = connection;
                        command.SelectCommand.CommandText = cmdText;
                        command.SelectCommand.CommandType = cmdType;
                        if (commandParameters != null && commandParameters.Length > 0)
                        {
                            foreach (SqlParameter parm in commandParameters)
                                command.SelectCommand.Parameters.AddWithValue(parm.ParameterName, parm.Value);
                        }
                        command.Fill(ds, "ds");
                    }
                    catch (System.Data.SqlClient.SqlException ex)
                    {
                        throw new Exception(ex.Message);
                    }
                    return ds;
                }
            }
            else
            {
                return null;
            }
        }

        

        /// <summary>
        /// ִ�в�ѯ��䣬����DataSet
        /// </summary>
        /// <param name="connectionString">�����ַ���</param>
        /// <param name="cmdText">��ѯ���</param>
        /// <returns>DataSet</returns>
        public static DataSet ExecuteDataSet(string connectionString, string cmdText, SqlParameter[] commandParameters)
        {
            return ExecuteDataSet(connectionString, cmdText, CommandType.Text, commandParameters);
        }

        

        

        /// <summary>
        /// ִ�в�ѯ��䣬����DataTable��ʡ�Բ�ѯ�ַ���
        /// </summary>
        /// <param name="cmdText"></param>
        /// <returns></returns>
        public static DataTable ExecuteDataTable(string connectionString, string cmdText)
        {
            DataSet ds = ExecuteDataSet(connectionString,cmdText, null);
            if (ds != null && ds.Tables.Count > 0)
                return ds.Tables[0];
            else
                return null;
        }
        public static DataTable ExecuteDataTable(string connectionString, string cmdText, CommandType cmdType, params SqlParameter[] commandParameters)
        {
            DataSet ds = ExecuteDataSet(connectionString,cmdText, cmdType, commandParameters);
            if (ds != null && ds.Tables.Count > 0)
                return ds.Tables[0];
            else
                return null;
        }

        /// <summary>
        /// ��ȡ��ҳ����
        /// </summary>
        /// <param name="connectionKey">����key</param>
        /// <param name="commandText">��ѯ��� ���в������ظ��ֶ�</param>
        /// <param name="orderBy">���� ������ ���ܰ���"."</param>
        /// <param name="parameterList">����ΪNull</param>
        /// <param name="pageIndex">��ʼҳ</param>
        /// <param name="pageSize">ÿҳ����</param>
        /// <param name="totalCount">��������</param>
        /// <returns></returns>
        public static DataTable ExecuteDataTablePaged(string connectionString, string commandText, string orderBy,  SqlParameter[] parameterList, int pageIndex, int pageSize, out int totalCount)
        {
            DataTable dt = new DataTable();
            totalCount = 0;
            DataSet dataSet = new DataSet();
            pageIndex = pageIndex > 1 ? pageIndex : 1;
            int start = (pageIndex - 1) * pageSize;
            int end = pageIndex * pageSize-1;
            //DataReader��ȡ�ĵ�ǰ�����е�����  
            int readToIndex = -1;
            string strSql = string.Concat(commandText, " Order By ", orderBy);
            using (SqlConnection connection = SqlHelper.GetConn(connectionString))
            {
                try
                { 
                    SqlCommand command = new  SqlCommand(strSql, connection);
                    command.Parameters.Clear();
                    if (parameterList != null)
                        command.Parameters.AddRange(parameterList);
                   
                    SqlDataReader dr = command.ExecuteReader();
                    int cols = dr.VisibleFieldCount;
                    //����DataTable�ṹ  
                    for (int i = 0; i < cols; i++)
                    {
                        dt.Columns.Add(new DataColumn(dr.GetName(i), dr.GetFieldType(i)));
                    }
                    //��ȡ���ݣ�������һ��һ����ӵ�DataTable  
                    while (dr.Read())
                    {
                        readToIndex++;
                        //��DataReaderָ���ڿ�ʼ�����ͽ�������������ʱ�Ŷ�ȡ���ݹ���DataRow  
                        //����ӵ�DataTable  
                        if (readToIndex >= start && readToIndex <= end)
                        {
                            DataRow row = dt.NewRow();
                            for (int i = 0; i < cols; i++)
                            {
                                row[i] = dr[i];
                            }
                            dt.Rows.Add(row);
                        }
                    }
                    dr.Close();
                    //SqlDataAdapter adapter = new SqlDataAdapter(command);
                    //adapter.Fill(dataSet);
                    ////����
                    command.CommandText = string.Format("SELECT COUNT(*) FROM ({0}) AS t", commandText);
                     totalCount = Convert.ToInt32(command.ExecuteScalar());
                     
                }
                catch (Exception ee) {
                    var aa = ee.Message;
                    connection.Close();
                }
            }
            
            return dt;
        }
        /// <summary>
        /// ִ�в�ѯ��䣬����DataTable��ʡ�Բ�ѯ�ַ���
        /// </summary>
        /// <param name="cmdText"></param>
        /// <returns></returns>
        public static DataRow ExecuteDataRow(string connectionString, string cmdText, CommandType cmdType, params SqlParameter[] commandParameters)
        {
            DataSet ds = ExecuteDataSet(connectionString,cmdText, cmdType,   commandParameters);
            if (ds != null && ds.Tables.Count > 0)
            {
                return ds.Tables[0].Rows[0];
            }
            return null;
        }

        #endregion
    }
}
