using System;
using System.Collections.Generic;
using System.Configuration;
using System.Data;
using System.Data.SqlClient;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Threading.Tasks;
using System.Web.Http;

namespace CCTVAPI.Controllers
{
    [RoutePrefix("api/Device")]
    public class DeviceController : ApiController
    { 
        
        [Route("UserDeviceInfo")]
        /// <summary>
        /// 
        /// </summary>
        /// <param name="avgData"></param>
        /// <returns></returns>
        public async Task<Result> UserDeviceInfo(Userinfo avgData)
        {
            Result _reslut = new Result()
            {
                Msg = "success",
                Code = 211
            };
           
            //记录后台数据库
            if(avgData!=null)
            {
                InserInfo("12", avgData.NickName, avgData.avatarUrl, avgData.Province, "12", DateTime.Now.ToString());
            }

            return _reslut;// Json<Result>(_reslut);
        }

        public int InserInfo(string userId,string userNicname, string wxPicurl, string wxCountry, string ProductId, string loginTime)
        {
            string conn = ConfigurationManager.AppSettings["CenterContractConn"].ToString();
            int flag = 0;
            string strInsert = @"insert into [UserInfoDevice]([UseID],[wxNickName],[wxPicurl],[wxCountry],[ProductId],[loginTime])
								values(@UseID,@wxNickName,@wxPicurl,@wxCountry,@ProductId,@loginTime)  select @@identity";

            List<SqlParameter> paramList = new List<SqlParameter>();
            paramList.Add(new SqlParameter("@UseID", SqlDbType.VarChar) { Value = SqlNull(userId) });
            paramList.Add(new SqlParameter("@wxNickName", SqlDbType.VarChar) { Value = SqlNull(userNicname) });
            paramList.Add(new SqlParameter("@wxPicurl", SqlDbType.VarChar) { Value = SqlNull(wxPicurl) });
            paramList.Add(new SqlParameter("@wxCountry", SqlDbType.VarChar) { Value = SqlNull(wxCountry) });
            paramList.Add(new SqlParameter("@ProductId", SqlDbType.VarChar) { Value = SqlNull(ProductId) });
            paramList.Add(new SqlParameter("@loginTime", SqlDbType.VarChar) { Value = SqlNull(loginTime) });


            try
            {

                flag = SqlHelper.ExecuteScalar(conn, strInsert, CommandType.Text, paramList.ToArray());
                return flag;
            }
            catch (Exception ex)
            {
            }
            return flag;
        }

        [Route("GetUserByProID")]
        [HttpPost]
        public async Task<Result> GetUserProInfo(string proid)
        {
            Result _reslut = new Result()
            {
                Msg = "success",
                Code = 1
            };

            //记录后台数据库
            if (proid != null)
            {
                List<Userinfo> _list = new List<Userinfo>();
                _list = this.GetUserInfo(proid);
                _reslut.data = _list;
            }

            return _reslut;// Json<Result>(_reslut);
        }
        
        public List<Userinfo> GetUserInfo(string productId)
        {
            string conn = ConfigurationManager.AppSettings["CenterContractConn"].ToString();
            List<Userinfo> _list = new List<Userinfo>();
            int flag = 0;
            string strInsert = @"SELECT TOP 1000 [id]
                              ,[UseID]
                              ,[wxNickName]
                              ,[wxPicurl]
                              ,[wxCountry]
                              ,[ProductId]
                              ,[loginTime]
                          FROM [RSKTSmall].[dbo].[UserInfoDevice] where		ProductId = @ProductId  order by loginTime desc";

            List<SqlParameter> paramList = new List<SqlParameter>();

            paramList.Add(new SqlParameter("@ProductId", SqlDbType.VarChar) { Value = SqlNull(productId) });

            try
            {

                DataTable dt = SqlHelper.ExecuteDataTable(conn, strInsert, CommandType.Text, paramList.ToArray());
                if(dt!=null && dt.Rows.Count>0)
                {
                    foreach(DataRow item in dt.Rows)
                    {
                        Userinfo _user = new Userinfo
                        {
                            avatarUrl = item["wxPicurl"].ToString(),
                            City = item["wxCountry"].ToString(),
                            NickName = item["wxNickName"].ToString(),
                            Province = item["wxCountry"].ToString(),
                            ProductId = item["ProductId"].ToString(),
                            LginTime = item["loginTime"].ToString(),
                            TempId = item["id"].ToString()
                        };
                        _list.Add(_user);
                    }
                }
            }
            catch (Exception ex)
            {
                throw ex;
            }
            return _list;
        }

        protected object SqlNull(object obj)
        {
            if (obj == null)
                return DBNull.Value;

            return obj;
        }
    }

    public class Userinfo
    {
        string _avatarUrl = string.Empty;

        string _city = string.Empty;

        string _country = string.Empty;

        string _nickName = string.Empty;

        string _province = string.Empty;

        string _productId = string.Empty;

        string _lginTime = string.Empty;

        string _tempId = string.Empty;

        /// <summary>
        /// 头像地址
        /// </summary>
        public string avatarUrl { get => _avatarUrl; set => _avatarUrl = value; }

        /// <summary>
        /// 城市
        /// </summary>
        public string City { get => _city; set => _city = value; }

        /// <summary>
        /// 国家
        /// </summary>
        public string Country { get => _country; set => _country = value; }

        /// <summary>
        /// 昵称
        /// </summary>
        public string NickName { get => _nickName; set => _nickName = value; }

        /// <summary>
        /// 省
        /// </summary>
        public string Province { get => _province; set => _province = value; }

        /// <summary>
        /// 产品id
        /// </summary>
        public string ProductId { get => _productId; set => _productId = value; }
        public string LginTime { get => _lginTime; set => _lginTime = value; }
        public string TempId { get => _tempId; set => _tempId = value; }
    }
}
