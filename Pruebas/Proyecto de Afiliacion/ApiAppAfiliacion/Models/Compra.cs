namespace ApiAppAfiliacion.Models
{
    public class Compra
    {
        public int Id { get; set; }
        public string Cliente { get; set; } = string.Empty;
        public DateTime Fecha { get; set; }
        public decimal Monto { get; set; }
    }
}
