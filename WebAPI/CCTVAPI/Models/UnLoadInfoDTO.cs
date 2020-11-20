using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace CCTVAPI.Models
{
    public class UnLoadInfoDTO
    {
        private Guid _taskId;

        private Guid _carId ;

        private int _needAll = 0;

        public Guid TaskId { get => _taskId; set => _taskId = value; }
        public Guid CarId { get => _carId; set => _carId = value; }
        /// <summary>
        /// 需要全部数据
        /// </summary>
        public int NeedAll { get => _needAll; set => _needAll = value; }
    }
}