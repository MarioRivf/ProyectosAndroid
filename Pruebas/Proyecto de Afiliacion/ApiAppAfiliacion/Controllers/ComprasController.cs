using ApiAppAfiliacion.Data;
using ApiAppAfiliacion.Models;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace ApiAppAfiliacion.Controllers
{
    [ApiController]
    [Route("api/compras")]
    //[controller]
    public class ComprasController : ControllerBase
    {
        private readonly ApiDbContext _context;

        public ComprasController(ApiDbContext context)
        {
            _context = context;
        }

        [HttpGet]
        public async Task<ActionResult<IEnumerable<Compra>>> Get()
        {
            return await _context.Compras.ToListAsync();
        }

        [HttpGet("{id}")]
        public async Task<ActionResult<Compra>> GetCompra(int id)
        {
            var compra = await _context.Compras.FindAsync(id);
            if (compra == null)
            {
                Console.WriteLine("No se encontró la compra");
                return NotFound();
            }

            Console.WriteLine("Compra encontrada");
            return compra;
        }

        [HttpPost]
        public async Task<ActionResult<Compra>> Post(Compra compra)
        {
            var comprasAnteriores = await _context.Compras
                .Where(c => c.NombreCliente == compra.NombreCliente)
                .CountAsync();

            if ((comprasAnteriores + 1) % 5 == 0)
            {
                compra.CodigoDescuento = GenerarCodigoDescuento();
            }

            _context.Compras.Add(compra);
            await _context.SaveChangesAsync();

            return CreatedAtAction(nameof(Get), new { id = compra.Id }, compra);
        }

        [HttpPut("{id}")]
        public async Task<IActionResult> Put(int id, Compra compra)
        {
            if (id != compra.Id)
            {
                return BadRequest("El ID de la compra no coincide.");
            }

            var compraExistente = await _context.Compras.FindAsync(id);
            if (compraExistente == null)
            {
                return NotFound();
            }

            // Actualiza los campos necesarios
            compraExistente.NombreCliente = compra.NombreCliente;
            compraExistente.Ubicacion = compra.Ubicacion;
            compraExistente.CodigoDescuento = compra.CodigoDescuento;

            try
            {
                await _context.SaveChangesAsync();
            }
            catch (DbUpdateConcurrencyException)
            {
                return StatusCode(500, "Error al actualizar la compra.");
            }

            return NoContent();
        }

        [HttpDelete("{id}")]
        public async Task<IActionResult> Delete(int id)
        {
            var compra = await _context.Compras.FindAsync(id);
            if (compra == null)
            {
                return NotFound("Compra no encontrada.");
            }

            _context.Compras.Remove(compra);
            await _context.SaveChangesAsync();

            return NoContent();
        }



        private string GenerarCodigoDescuento()
        {
            var letras = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
            var random = new Random();
            return $"{letras[random.Next(26)]}{letras[random.Next(26)]}{random.Next(100, 999)}";
        }

    }
}
