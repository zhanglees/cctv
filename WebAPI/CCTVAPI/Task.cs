//------------------------------------------------------------------------------
// <auto-generated>
//    此代码是根据模板生成的。
//
//    手动更改此文件可能会导致应用程序中发生异常行为。
//    如果重新生成代码，则将覆盖对此文件的手动更改。
// </auto-generated>
//------------------------------------------------------------------------------

namespace CCTVAPI
{
    using Newtonsoft.Json;
    using System;
    using System.Collections.Generic;
    
    public partial class Task
    {
        public Task()
        {
            this.EquipmentOrder = new HashSet<EquipmentOrder>();
        }
    
        public System.Guid id { get; set; }
        public string TaskName { get; set; }
        public string TaskNum { get; set; }
        public Nullable<int> State { get; set; }
    
        [JsonIgnore]
        public virtual ICollection<EquipmentOrder> EquipmentOrder { get; set; }
    }
}
