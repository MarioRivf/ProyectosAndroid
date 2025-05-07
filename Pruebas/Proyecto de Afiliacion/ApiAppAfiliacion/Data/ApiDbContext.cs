using ApiAppAfiliacion.Models;
using Microsoft.EntityFrameworkCore;
using System.Collections.Generic;

namespace ApiAppAfiliacion.Data
{
    public class ApiDbContext : DbContext
    {
        public ApiDbContext(DbContextOptions<ApiDbContext> options) : base(options) { }

        public DbSet<Compra> Compras { get; set; }
    }
}
