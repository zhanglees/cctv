using CCTVAPI.Models;
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
using System.Web.Http.Cors;

namespace CCTVAPI.Controllers
{
    [RoutePrefix("api/EquipmentOrder")]
   // [EnableCors(origins: "*", headers: "*", methods: "GET,POST,PUT,DELETE")]
    public class EquipmentOrderController : ApiController
    {
        CCTVDeviceUHFEntities _efDb = null;

        [Route("GetEquipmentOrderList")]
        [HttpPost]
        /// <summary>
        /// 得到清单列表信息
        /// </summary>
        /// <param name="avgData"></param>
        /// <returns></returns>
        public async Task<Result> GetEquipmentOrderList(Task task)
        {
            Result _reslut = new Result()
            {
                Msg = "false",
                Code = 1
            };

            try
            {
                //记录后台数据库
                // EquipmentOrderDTO _orderDto = new EquipmentOrderDTO();
                List<orderItems> _items = new List<orderItems>();
                _efDb = new CCTVDeviceUHFEntities();
                _items = _efDb.orderItems.Where(i => i.taskId == task.id).ToList();
                var hasCarDatas = _efDb.orderItemsByCar.Where(i => i.taskId == task.id).ToList();
                //for(int i =0;i<_items.Count;i++)
                //{
                //    if(hasCarDatas.Any(o=>o.EquipmentOrderId == _items[i].EquipmentOrderId))
                //    {
                _items= _items.FindAll(delegate (orderItems data) { if (hasCarDatas.Any(t => t.orderItemId == data.orderItemId)) { return false; } else { return true; } });
                //    }
                //}


                _reslut.data = _items;
                _reslut.Msg = "success";
                _reslut.Code = 0;
            }
            catch(Exception ex)
            {
                _reslut.Msg = ex.Message;
            }
          
            return _reslut;// Json<Result>(_reslut);
        }

        [Route("GetOrders")]
        [HttpPost]
        public async Task<Result> GetOrderInfo()
        {
            Result _reslut = new Result()
            {
                Msg = "false",
                Code = 1
            };

            try
            {
                //记录后台数据库                             
                using (_efDb = new CCTVDeviceUHFEntities())
                {
                    try
                    {
                        var data = _efDb.EquipmentOrder.Where(item => item.TaskId != null).ToList();

                        _reslut.data = data;
                        _reslut.Code = 0;
                        _reslut.Msg = "success";
                    }
                    catch (Exception ex)
                    {
                        _reslut.Code = 1;
                        _reslut.Msg = ex.Message;
                    }
                }
                return _reslut;

            }
            catch (Exception ex)
            {
                _reslut.Msg = ex.Message;
            }

            return _reslut;// Json<Result>(_reslut);
        }


        [Route("CreateTask")]
        [HttpPost]
        /// <summary>
        /// 得到清单列表信息
        /// </summary>
        /// <param name="avgData"></param>
        /// <returns></returns>
        public async Task<Result> CreateTask(Task task)
        {
            Result _reslut = new Result()
            {
                Msg = "false",
                Code = 1
            };

            try
            {
                //记录后台数据库                             
                if(task.TaskNum!=null)
                {
                    task.id = Guid.NewGuid();
                    task.State = 0;
                }
                using (_efDb = new CCTVDeviceUHFEntities())
                {
                    try
                    {
                        _efDb.Task.Add(task);
                        _efDb.SaveChanges();
                        _reslut.Code = 0;
                        _reslut.Msg = "success";
                    }
                    catch(Exception ex)
                    {
                        _reslut.Code = 1;
                        _reslut.Msg = ex.Message;
                    }                   
                }
                return _reslut;
                
            }
            catch (Exception ex)
            {
                _reslut.Msg = ex.Message;
            }

            return _reslut;// Json<Result>(_reslut);
        }

        [Route("ModifyTask")]
        [HttpPost]
        public async Task<Result> ModifyTask(Task task)
        {
            Result _reslut = new Result()
            {
                Msg = "false",
                Code = 1
            };

            try
            {
                //记录后台数据库                             
                if (task.TaskNum == null)
                {
                    return _reslut;
                }
                using (_efDb = new CCTVDeviceUHFEntities())
                {
                    try
                    {
                        Task tempData = new Task() { id = task.id };
                        _efDb.Task.Attach(tempData);
                        _efDb.SaveChanges();
                        _reslut.Code = 0;
                        _reslut.Msg = "success";
                    }
                    catch (Exception ex)
                    {
                        _reslut.Code = 1;
                        _reslut.Msg = ex.Message;
                    }
                }
                return _reslut;

            }
            catch (Exception ex)
            {
                _reslut.Msg = ex.Message;
            }

            return _reslut;// Json<Result>(_reslut);
        }


        [Route("GetTaskList")]
        [HttpPost]
        /// <summary>
        /// 
        /// </summary>
        /// <returns></returns>
        public async Task<Result> GetTaskList()
        {
            Result _reslut = new Result()
            {
                Msg = "false",
                Code = 1
            };

            //记录后台数据库
            try
            {
                List<Task> _task = new List<Task>();
                _efDb = new CCTVDeviceUHFEntities();
                //没有结束的弄出来
                _task = _efDb.Task.Where(i => i.State != 7).ToList();
                _reslut.data = _task;
                _reslut.Msg = "success";
                _reslut.Code = 0;
            }
            catch(Exception ex)
            {
                _reslut.Msg = ex.Message;
            }
           

            return _reslut;// Json<Result>(_reslut);
        }

        /// <summary>
        /// 统计装载信息 （暂时不记录历史）
        /// </summary>
        /// <param name="dto"></param>
        /// <returns></returns>
        [Route("TotalCarLoad")]
        [HttpPost]
        public async Task<Result> TotalCarLoad(EquipmentOrderItemDTO dto)
        {
            Result _reslut = new Result()
            {
                Msg = "false",
                Code = 1
            };

            string _strInsert = @"insert into [CarInfoEquipmentOrderItem]([CarInfoID],[EquipmentOrderItem])
								values(@CarInfoID,@EquipmentOrderItem)  select @@identity";
            string _strDelete = @"DELETE FROM [dbo].[CarInfoEquipmentOrderItem]
                                WHERE CarInfoID = @CarInfoID and EquipmentOrderItem = @EquipmentOrderItem";
            try
            {
                if (dto != null && dto.Items.Count > 0)
                {
                    using (_efDb = new CCTVDeviceUHFEntities())
                    {
                        string _car = string.Empty;
                        string _equipmentId = string.Empty;
                        string _sql = _strInsert;
                        foreach (var info in dto.Items)
                        {
                            _car = info.CarId;
                            _equipmentId = info.OrderItemId;

                            List<SqlParameter> paramList = new List<SqlParameter>();
                            paramList.Add(new SqlParameter("@CarInfoID", SqlDbType.VarChar) { Value = SqlNull(_car) });
                            paramList.Add(new SqlParameter("@EquipmentOrderItem", SqlDbType.VarChar) { Value = SqlNull(_equipmentId) });

                            if (dto.LoadType == 1)
                            {
                                _sql = _strDelete;
                            }
                            else
                            {
                                string conn = ConfigurationManager.AppSettings["CCTVAPI"].ToString();
                                string check = String.Format("select * from CarInfoEquipmentOrderItem where EquipmentOrderItem='{0}'", info.OrderItemId);
                                // int flag = _efDb.Database.ExecuteSqlCommand(check);
                                DataTable dt = SqlHelper.ExecuteDataTable(conn, check);
                                if (dt!=null && dt.Rows.Count>0) continue;
                            }
                            var message = _efDb.Database.ExecuteSqlCommand(_sql, paramList.ToArray());
                            
                        }
                    }
                }
                _reslut.Code = 0;_reslut.Msg = "success";_reslut.data = "success";
            }
            catch(Exception ex)
            {
                _reslut.Msg = ex.Message;_reslut.data = ex.Message;
            }
            
            return _reslut;
        }

        /// <summary>
        /// 统计装载信息 （暂时不记录历史）
        /// </summary>
        /// <param name="dto"></param>
        /// <returns></returns>
        [Route("GetCarLoad")]
        [HttpPost]
        public async Task<Result> GetCarLoad(UnLoadInfoDTO dto)
        {
            Result _reslut = new Result()
            {
                Msg = "false",
                Code = 1
            };
        
            try
            {
                if (dto != null)
                {
                    using (_efDb = new CCTVDeviceUHFEntities())
                    {
                        string _car = string.Empty;
                        var _items = _efDb.orderItemsByCar.Where(i => i.taskId == dto.TaskId && i.carId == dto.CarId).ToList();

                        _reslut.data = _items;
                    }
                }
                _reslut.Code = 0; _reslut.Msg = "success";
            }
            catch (Exception ex)
            {
                _reslut.Msg = ex.Message;
            }

            return _reslut;
        }

        [Route("GetRfit")]
        [HttpPost]
        /// <summary>
        /// 得到Rfid 集合
        /// </summary>
        /// <param name="dto">查询一个rfid 标签</param>
        /// <returns></returns>
        public async Task<Result> GetRfit(RFID dto)
        {
            Result _reslut = new Result()
            {
                Msg = "false",
                Code = 1
            };

            try
            {
                if (dto != null)
                {
                    using (_efDb = new CCTVDeviceUHFEntities())
                    {
                        var _items = _efDb.RFID.Where(i => i.id == dto.id).FirstOrDefault();
                        _reslut.data = _items;
                    }
                }
                else
                {
                    using (_efDb = new CCTVDeviceUHFEntities())
                    {
                        var _items = _efDb.RFID.Where(i => i.id != null).ToList();
                        _reslut.data = _items;
                    }
                }
                _reslut.Code = 0; _reslut.Msg = "success";
            }
            catch (Exception ex)
            {
                _reslut.Msg = ex.Message;
            }

            return _reslut;
        }

        [Route("CreatRfid")]
        [HttpPost]
        /// <summary>
        /// 创建
        /// </summary>
        /// <param name="dto">创建rfid 标签</param>
        /// <returns></returns>
        public async Task<Result> CreatRfid(RFID dto)
        {
            Result _reslut = new Result()
            {
                Msg = "false",
                Code = 1
            };

            try
            {
                //记录后台数据库                             
                if (dto.EPCNum != null)
                {
                    dto.id = Guid.NewGuid();
                    dto.CreateTime = DateTime.Now;
                }
                using (_efDb = new CCTVDeviceUHFEntities())
                {
                    try
                    {
                        _efDb.RFID.Add(dto);
                        _efDb.SaveChanges();
                        _reslut.Code = 0;
                        _reslut.Msg = "success";
                    }
                    catch (Exception ex)
                    {
                        _reslut.Code = 1;
                        _reslut.Msg = ex.Message;
                    }
                }
                return _reslut;

            }
            catch (Exception ex)
            {
                _reslut.Msg = ex.Message;
            }

            return _reslut;// Json<Result>(_reslut);
        }

        protected object SqlNull(object obj)
        {
            if (obj == null)
                return DBNull.Value;

            return obj;
        }
    }
}
