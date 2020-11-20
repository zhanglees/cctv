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
    /// 数据库访问类
    /// </summary>
    public static class SqlHelper
    {
        #region 变量
        public static int CommandTimeout = 360;
        /// <summary>
        /// 数据库连接对象
        /// </summary>
        public static SqlConnection Con;
        /// <summary>
        /// 程序集的名称
        /// </summary>
        ///private static string _assemblyName = "Entity";
        // 在web.config配置文件中添加连库字符串
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

        #region 获取或设置数据库连接对象

        /// <summary>
        /// 获取或设置数据库连接对象
        /// </summary>
        [Obsolete("待优化 Con 连接数是否需要考虑。daniel.zhu ")]
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

        #region 方法
        #region 产生一个事务并开始
        [Obsolete("待优化 如果使用Con属性 会产生【事务不是与当前连接无关联,就是已完成】的错误。 daniel.zhu")]
        /// <summary> 
        /// 产生一个事务并开始 
        /// </summary> 
        /// <returns>返回此事务</returns> 
        public static SqlTransaction BeginTransaction(string connectionString)
        {
            SqlHelper.Con = SqlHelper.GetConn(connectionString); //这句话永远创建一个新的链接 搞什么玩意。
            SqlTransaction tran = SqlHelper.Con.BeginTransaction();
            return tran;
        }
        #endregion 
        
        #region  提交事务
        /// <summary> 
        /// 提交事务 
        /// </summary> 
        public static void CommitTransaction(SqlTransaction tranc)
        {
          
            tranc.Commit();
            SqlHelper.Con.Close();
        }
        #endregion 

        /// <summary>
        /// 执行返回一行一列的数据库操作
        /// </summary>
        /// <param name="commandText">SQL语句或存储过程名</param>
        /// <param name="commandType">SQL命令类型</param>
        /// <param name="param">SQL命令参数数组</param>
        /// <returns>第一行第一列的记录</returns>
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
        /// 执行返回一行一列的数据库操作
        /// </summary>
        /// <param name="commandText">SQL语句或存储过程名</param>
        /// <param name="commandType">SQL命令类型</param>
        /// <param name="param">SQL命令参数数组</param>
        /// <returns>第一行第一列的记录</returns>
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
        /// 执行返回一行一列的数据库操作
        /// </summary>
        /// <param name="commandText">SQL语句或存储过程名</param>
        /// <param name="commandType">SQL命令类型</param>
        /// <param name="param">SQL命令参数数组</param>
        /// <returns>第一行第一列的记录</returns>
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
        /// 执行不查询的数据库操作
        /// </summary>
        /// <param name="commandText">SQL语句或存储过程名</param>
        /// <param name="commandType">SQL命令类型</param>
        /// <param name="param">SQL命令参数数组</param>
        /// <returns>受影响的行数</returns>
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
        /// 执行不查询的数据库操作
        /// </summary>
        /// <param name="commandText">SQL语句或存储过程名</param>
        /// <param name="commandType">SQL命令类型</param>
        /// <param name="param">SQL命令参数数组</param>
        /// <returns>受影响的行数</returns>
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
        /// 执行返回一条记录的泛型对象
        /// </summary>
        /// <typeparam name="T">泛型类型</typeparam>
        /// <param name="reader">只进只读对象</param>
        /// <returns>泛型对象</returns>
        private static T ExecuteDataReader<T>(IDataReader reader)
        {
            T obj = default(T);
            try
            {
                Type type = typeof(T);
                obj = (T)Activator.CreateInstance(type);//从当前程序集里面通过反射的方式创建指定类型的对象
                //obj = (T)Assembly.Load(SqlHelper._assemblyName).CreateInstance(SqlHelper._assemblyName + "." + type.Name);//从另一个程序集里面通过反射的方式创建指定类型的对象
                PropertyInfo[] propertyInfos = type.GetProperties();//获取指定类型里面的所有属性
                foreach (PropertyInfo propertyInfo in propertyInfos)
                {
                    for (int i = 0; i < reader.FieldCount; i++)
                    {
                        string fieldName = reader.GetName(i);
                        if (fieldName.ToLower() == propertyInfo.Name.ToLower())
                        {
                            object val = reader[propertyInfo.Name];//读取表中某一条记录里面的某一列信息
                            if (val != null && val != DBNull.Value)
                                propertyInfo.SetValue(obj, val, null);//给对象的某一个属性赋值
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
        /// 执行返回一条记录的泛型对象
        /// </summary>
        /// <typeparam name="T">泛型类型</typeparam>
        /// <param name="commandText">SQL语句或存储过程名</param>
        /// <param name="commandType">SQL命令类型</param>
        /// <param name="param">SQL命令参数数组</param>
        /// <returns>实体对象</returns>
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
        /// 执行返回多条记录的泛型集合对象
        /// </summary>
        /// <typeparam name="T">泛型类型</typeparam>
        /// <param name="commandText">SQL语句或存储过程名</param>
        /// <param name="commandType">SQL命令类型</param>
        /// <param name="param">SQL命令参数数组</param>
        /// <returns>泛型集合对象</returns>
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
        /// 传入要执行的非查询SQL语句集合
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

        #region ExecuteDataSet(执行查询语句，返回DataSet)

        /// <summary>   执行查询语句，返回DataSet
        /// </summary>
        /// <param name="connectionString">连接字符串</param>
        /// <param name="cmdText">执行语句</param>
        /// <param name="cmdType">执行方式</param>
        /// <param name="commandParameters">参数集合</param>
        /// <returns>数据集</returns>
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
        /// 执行查询语句，返回DataSet
        /// </summary>
        /// <param name="connectionString">连接字符串</param>
        /// <param name="cmdText">查询语句</param>
        /// <returns>DataSet</returns>
        public static DataSet ExecuteDataSet(string connectionString, string cmdText, SqlParameter[] commandParameters)
        {
            return ExecuteDataSet(connectionString, cmdText, CommandType.Text, commandParameters);
        }

        

        

        /// <summary>
        /// 执行查询语句，返回DataTable，省略查询字符串
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
        /// 获取分页数据
        /// </summary>
        /// <param name="connectionKey">连接key</param>
        /// <param name="commandText">查询语句 其中不能有重复字段</param>
        /// <param name="orderBy">排序 必须有 不能包含"."</param>
        /// <param name="parameterList">可以为Null</param>
        /// <param name="pageIndex">起始页</param>
        /// <param name="pageSize">每页数量</param>
        /// <param name="totalCount">返回总数</param>
        /// <returns></returns>
        public static DataTable ExecuteDataTablePaged(string connectionString, string commandText, string orderBy,  SqlParameter[] parameterList, int pageIndex, int pageSize, out int totalCount)
        {
            DataTable dt = new DataTable();
            totalCount = 0;
            DataSet dataSet = new DataSet();
            pageIndex = pageIndex > 1 ? pageIndex : 1;
            int start = (pageIndex - 1) * pageSize;
            int end = pageIndex * pageSize-1;
            //DataReader读取的当前数据行的索引  
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
                    //构造DataTable结构  
                    for (int i = 0; i < cols; i++)
                    {
                        dt.Columns.Add(new DataColumn(dr.GetName(i), dr.GetFieldType(i)));
                    }
                    //读取数据，将数据一行一行添加到DataTable  
                    while (dr.Read())
                    {
                        readToIndex++;
                        //当DataReader指针在开始索引和结束索引闭区间时才读取数据构造DataRow  
                        //并添加到DataTable  
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
                    ////总数
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
        /// 执行查询语句，返回DataTable，省略查询字符串
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
