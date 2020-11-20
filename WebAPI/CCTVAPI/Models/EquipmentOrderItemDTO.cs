using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace CCTVAPI.Models
{
    /// <summary>
    /// 装载dto
    /// </summary>
    public class EquipmentOrderItemDTO
    {
        private List<CarOrderItemInfo> _items = new List<CarOrderItemInfo>();

        private int _loadType = 0;

        /// <summary>
        /// 装载车的条目
        /// </summary>
        public List<CarOrderItemInfo> Items { get => _items; set => _items = value; }

        /// <summary>
        /// 状态0 装载 1卸载
        /// </summary>
        public int LoadType { get => _loadType; set => _loadType = value; }
    }

    /// <summary>
    /// 装载信息
    /// </summary>
    public class CarOrderItemInfo
    {
        private string _carId = string.Empty;

        private string _orderItemId = string.Empty;

        /// <summary>
        /// 货车id
        /// </summary>
        public string CarId { get => _carId; set => _carId = value; }
        /// <summary>
        /// 订单条目id
        /// </summary>
        public string OrderItemId { get => _orderItemId; set => _orderItemId = value; }
    }
}