using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.Web;

namespace CCTVAPI.Controllers
{
    public class Result
    {
        private string _msg;
        //private bool _success;
        private int _code = 0;
        //private int _errno = 0;

        private string _error;

        private int _errno = 0;
        private string _requestId = string.Empty;
        private int _resultCode = 0;
        private string _resultInfo = string.Empty;


        /// <summary>
        /// 返回码
        /// </summary>
        [JsonProperty("code")]
        public int Code
        {
            get
            {
                return _code;
            }

            set
            {
                _code = value;
            }
        }

        #region 错误信息

        /// <summary>
        ///    错误信息 
        /// </summary>
        [JsonProperty("error")]
        public string Error
        {
            get
            {
                if (string.IsNullOrEmpty(_error) && InnerException != null)
                {
                    return InnerException.Message;
                }
                else if (!string.IsNullOrEmpty(_error))
                {
                    return _error;
                }
                else
                {
                    return string.Empty;
                }
            }
            set { _error = value; }
        }

        #endregion

        #region 消息，非异常返回

        /// <summary>
        ///     消息，非异常返回
        /// </summary>
        [JsonProperty("msg")]
        public string Msg
        {
            get
            {
                if (_msg == null)
                {
                    return string.Empty;
                }
                return _msg;
            }
            set { _msg = value; }
        }

        #endregion

        #region 日志的消息

        /// <summary>
        ///     日志的消息
        /// </summary>
        [IgnoreDataMember, JsonIgnore]
        public string LogMessage { get; set; }

        #endregion

        #region 21返还值信息
        /// <summary>
        /// 21返回值
        /// </summary>
        public string ErrorMsg { get => ResultInfo; set => ResultInfo = value; }


        public object ReturnData { get => data; }
        #endregion

        /// <summary>
        ///     返回结果
        /// </summary>
        public Result()
        {
        }


        /// <summary>
        ///     异常信息
        /// </summary>
        [IgnoreDataMember, JsonIgnore]
        public Exception InnerException { get; set; }

        /// <summary>
        /// 返回的数据
        /// </summary>
        public object data { get; set; }

        [JsonProperty("errno")]
        public int Errno { get => _errno; set => _errno = value; }

        [JsonProperty("requestId")]
        public string RequestId { get => _requestId; set => _requestId = value; }

        [JsonProperty("resultCode")]
        public int ResultCode { get => _resultCode; set => _resultCode = value; }

        [JsonProperty("resultInfo")]
        public string ResultInfo { get => _resultInfo; set => _resultInfo = value; }
    }

    /// <summary>
    ///     返回结果
    /// </summary>
    /// <remarks>业务处理结果</remarks>
    public class Result<T> : Result
    {
        public Result()
        {
        }

        /// <summary>
        ///     返回结果
        /// </summary>
        public Result(T data)
        {
            Data = data;
        }

        /// <summary>
        ///     数据
        /// </summary>
        public T Data { get; set; }
    }
}