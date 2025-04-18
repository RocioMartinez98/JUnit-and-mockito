package org.rmartinez.junitapp.ejemplo.models;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.rmartinez.junitapp.ejemplo.exceptions.DineroInsuficienteException;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class CuentaTest {

    @Test
    void test_nombre_cuenta() {
        Cuenta cuenta = new Cuenta("Rocio", new BigDecimal("1000.12345"));
        //cuenta.setPersona("Rocio");
        String esperado = "Rocio";
        String real = cuenta.getPersona();
        assertNotNull(real);
        assertEquals(esperado, real); //Es mejor usar assertEquals que assertTrue
        assertTrue(real.equals("Rocio"));
    }

    @Test
    void test_saldo_cuenta() {
        Cuenta cuenta = new Cuenta("Rocio", new BigDecimal("1000.12345"));
        assertNotNull(cuenta.getSaldo());
        assertEquals(1000.12345, cuenta.getSaldo().doubleValue());
        assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    void testReferenciaCuenta() {
        Cuenta cuenta = new Cuenta("Dafne", new BigDecimal("1000.741"));
        Cuenta cuenta2 = new Cuenta("Dafne", new BigDecimal("1000.741"));

        //assertNotEquals(cuenta2, cuenta);
        //modifiqué el metodo equals de la clase cuenta para que esto de true
        assertEquals(cuenta2, cuenta); //compara por instancia, si son distintas es false xq son distintas dir de mem

    }

    @Test
    void testDebitoCuenta() {
        Cuenta cuenta = new Cuenta("Rocio", new BigDecimal("1000.12345"));
        cuenta.debito(new BigDecimal(100));
        assertNotNull(cuenta.getSaldo());
        assertEquals(900, cuenta.getSaldo().intValue());
        assertEquals("900.12345",cuenta.getSaldo().toPlainString());
    }

    @Test
    void testCreditoCuenta() {
        Cuenta cuenta = new Cuenta("Rocio", new BigDecimal("1000.12345"));
        cuenta.credito(new BigDecimal(100));
        assertNotNull(cuenta.getSaldo());
        assertEquals(1100, cuenta.getSaldo().intValue());
        assertEquals("1100.12345",cuenta.getSaldo().toPlainString());
    }

    @Test
    void testDineroInsuficienteExceptionCuenta() {
        Cuenta cuenta = new Cuenta("Rocio", new BigDecimal("1000.12345"));
        Exception exception = assertThrows(DineroInsuficienteException.class, ()-> {
            cuenta.debito(new BigDecimal(1500));
        });
        String actual = exception.getMessage();
        String esperado = "Dinero insuficiente";
        assertEquals(esperado, actual);
    }

    @Test
    void testTransferirDineroCuentas() {
        Cuenta cuenta1 = new Cuenta("Rocio", new BigDecimal("2500"));
        Cuenta cuenta2 = new Cuenta("Dafne", new BigDecimal("1500.8989"));

        Banco banco = new Banco();
        banco.setNombre("BNA");
        banco.transferir(cuenta2, cuenta1, new BigDecimal(500));

        assertEquals("1000.8989", cuenta2.getSaldo().toPlainString());
        assertEquals("3000", cuenta1.getSaldo().toPlainString());
    }

    @Test
    void testRelacionBancoCuentas() {
        Cuenta cuenta1 = new Cuenta("Rocio", new BigDecimal("2500"));
        Cuenta cuenta2 = new Cuenta("Dafne", new BigDecimal("1500.8989"));

        Banco banco = new Banco();
        banco.addCuenta(cuenta1);
        banco.addCuenta(cuenta2);
        banco.setNombre("BNA");
        banco.transferir(cuenta2, cuenta1, new BigDecimal(500));

        assertEquals("1000.8989", cuenta2.getSaldo().toPlainString());
        assertEquals("3000", cuenta1.getSaldo().toPlainString());

        assertEquals(2, banco.getCuentas().size());
        assertEquals("BNA", cuenta1.getBanco().getNombre());
        assertEquals("Rocio", banco.getCuentas().stream().filter
                (c -> c.getPersona().equals("Rocio")).
                findFirst().get().getPersona());

        assertTrue(banco.getCuentas().stream().
                anyMatch(c -> c.getPersona().equals("Dafne")));
    }

}