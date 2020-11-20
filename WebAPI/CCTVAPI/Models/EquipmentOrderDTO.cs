using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace CCTVAPI
{
    /// <summary>
    /// DTO视图用 
    /// </summary>
    public class EquipmentOrderDTO : IModel
    {
        private Task _task = null;

        /// <summary>
        /// 一个任务
        /// </summary>
        public Task Task { get => _task; set => _task = value; }
        public List<EquipmentOrder> Orders { get => _orders; set => _orders = value; }

        /// <summary>
        /// 清单具体信息
        /// </summary>
        public List<orderItems> OrderItem { get => _orderItem; set => _orderItem = value; }

        /// <summary>
        /// 几个清单
        /// </summary>
        List<EquipmentOrder> _orders = null;

        List<orderItems> _orderItem = null;

        public void KeyDirect()
        {
            throw new NotImplementedException();
        }
    }
}