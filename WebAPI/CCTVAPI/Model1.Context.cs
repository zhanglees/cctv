﻿//------------------------------------------------------------------------------
// <auto-generated>
//    此代码是根据模板生成的。
//
//    手动更改此文件可能会导致应用程序中发生异常行为。
//    如果重新生成代码，则将覆盖对此文件的手动更改。
// </auto-generated>
//------------------------------------------------------------------------------

namespace CCTVAPI
{
    using System;
    using System.Data.Entity;
    using System.Data.Entity.Infrastructure;
    
    public partial class CCTVDeviceUHFEntities : DbContext
    {
        public CCTVDeviceUHFEntities()
            : base("name=CCTVDeviceUHFEntities")
        {
        }
    
        protected override void OnModelCreating(DbModelBuilder modelBuilder)
        {
            throw new UnintentionalCodeFirstException();
        }
    
        public DbSet<CarInfo> CarInfo { get; set; }
        public DbSet<DeviceInfo> DeviceInfo { get; set; }
        public DbSet<DriverInfo> DriverInfo { get; set; }
        public DbSet<EquipmentOrder> EquipmentOrder { get; set; }
        public DbSet<EquipmentOrderItem> EquipmentOrderItem { get; set; }
        public DbSet<EquipmentOrderItemDevices> EquipmentOrderItemDevices { get; set; }
        public DbSet<RFID> RFID { get; set; }
        public DbSet<RFIDBoard> RFIDBoard { get; set; }
        public DbSet<sysdiagrams> sysdiagrams { get; set; }
        public DbSet<Task> Task { get; set; }
        public DbSet<orderInfo> orderInfo { get; set; }
        public DbSet<orderItems> orderItems { get; set; }
        public DbSet<HistoryCarLoad> HistoryCarLoad { get; set; }
        public DbSet<orderItemsByCar> orderItemsByCar { get; set; }
    }
}
