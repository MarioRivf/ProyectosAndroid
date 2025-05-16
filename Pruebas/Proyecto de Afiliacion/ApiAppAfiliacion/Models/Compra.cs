namespace ApiAppAfiliacion.Models
{
    public class Compra
    {
        public int Id { get; set; }
        public string NombreCliente { get; set; }
        public string Ubicacion { get; set; }
        public DateTime FechaHora { get; set; }
        public string? CodigoDescuento { get; set; }

    }
}
