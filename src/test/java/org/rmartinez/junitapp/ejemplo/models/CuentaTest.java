package org.rmartinez.junitapp.ejemplo.models;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.rmartinez.junitapp.ejemplo.exceptions.DineroInsuficienteException;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

class CuentaTest {

    Cuenta cuenta;

    @BeforeEach
    void initMetodoTest()
    {
        this.cuenta = new Cuenta("Rocio", new BigDecimal("1000.12345"));

        System.out.println("Iniciando el método de prueba.");

    }

    @AfterEach
    void tearDown() {
        System.out.println("Finalizando el metodo de prueba.");
    }

    @BeforeAll
    static void beforeAll() {
        System.out.println("Inicializando el test");
    }

    @AfterAll
    static void afterAll() {
        System.out.println("Finalizando el test");
    }

    @Nested
    class CuentaTestNombreSaldo{
        @Test
        @DisplayName("Prueba del nombre de la cuenta")
        void test_nombre_cuenta() {
            //Cuenta cuenta = new Cuenta("Rocio", new BigDecimal("1000.12345"));
            //cuenta.setPersona("Rocio");
            String esperado = "Rocio";
            String real = cuenta.getPersona();
            assertNotNull(real, () -> "La cuenta no puede ser nula");
            assertEquals(esperado, real, () -> "El nombre de la cuenta no es el esperado: se esperaba " + esperado + " y fue " + real); //Es mejor usar assertEquals que assertTrue
            assertTrue(real.equals("Rocio"), () ->"Nombre cuenta esperada debe ser igual a la real");
        }

        @Test
        void test_saldo_cuenta() {
            //cuenta = new Cuenta("Rocio", new BigDecimal("1000.12345"));
            assertNotNull(cuenta.getSaldo());
            assertEquals(1000.12345, cuenta.getSaldo().doubleValue());
            assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

        @Test
        @DisplayName("Probando referencias que sean iguales con método equals")
        void testReferenciaCuenta() {
            cuenta = new Cuenta("Dafne", new BigDecimal("1000.741"));
            Cuenta cuenta2 = new Cuenta("Dafne", new BigDecimal("1000.741"));

            //assertNotEquals(cuenta2, cuenta);
            //modifiqué el metodo equals de la clase cuenta para que esto de true
            assertEquals(cuenta2, cuenta); //compara por instancia, si son distintas es false xq son distintas dir de mem

        }
    }

    @Nested
    class CuentaOperacionesTest{
        @Test
        void testDebitoCuenta() {
            //cuenta = new Cuenta("Rocio", new BigDecimal("1000.12345"));
            cuenta.debito(new BigDecimal(100));
            assertNotNull(cuenta.getSaldo());
            assertEquals(900, cuenta.getSaldo().intValue());
            assertEquals("900.12345",cuenta.getSaldo().toPlainString());
        }

        @Test
        void testCreditoCuenta() {
            //cuenta = new Cuenta("Rocio", new BigDecimal("1000.12345"));
            cuenta.credito(new BigDecimal(100));
            assertNotNull(cuenta.getSaldo());
            assertEquals(1100, cuenta.getSaldo().intValue());
            assertEquals("1100.12345",cuenta.getSaldo().toPlainString());
        }

        @Test
        void testDineroInsuficienteExceptionCuenta() {
            //cuenta = new Cuenta("Rocio", new BigDecimal("1000.12345"));
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

    }


    @Test
    //@Disabled //para ignorar un test que está fallando
    @DisplayName("probando relaciones entre cuentas y banco con assertAll")
    void testRelacionBancoCuentas() {
        //fail() //para hacer que falle el test
        Cuenta cuenta1 = new Cuenta("Rocio", new BigDecimal("2500"));
        Cuenta cuenta2 = new Cuenta("Dafne", new BigDecimal("1500.8989"));

        Banco banco = new Banco();
        banco.addCuenta(cuenta1);
        banco.addCuenta(cuenta2);
        banco.setNombre("BNA");
        banco.transferir(cuenta2, cuenta1, new BigDecimal(500));

        assertAll(() -> {
                    assertEquals("1000.8989", cuenta2.getSaldo().toPlainString(), () -> "Valor inesperado cuenta2");
                },
                () -> {
                    assertEquals("3000", cuenta1.getSaldo().toPlainString(), () -> "Valor inesperado de la cuenta1");
                },
                () -> {
                    assertEquals(2, banco.getCuentas().size(), () ->"El banco no tiene las cuentas esperadas");
                },
                () -> {
                    assertEquals("BNA", cuenta1.getBanco().getNombre(), () -> "Nombre del banco inadecuado");
                },
                () -> {
                    assertEquals("Rocio", banco.getCuentas().stream().filter
                                    (c -> c.getPersona().equals("Rocio")).
                            findFirst().get().getPersona());
                },
                () -> {
                    assertTrue(banco.getCuentas().stream().
                            anyMatch(c -> c.getPersona().equals("Dafne")));
                }
        );

        //assertEquals("1000.8989", cuenta2.getSaldo().toPlainString());
        //assertEquals("3000", cuenta1.getSaldo().toPlainString());
        //assertEquals(2, banco.getCuentas().size());
        //assertEquals("BNA", cuenta1.getBanco().getNombre());
        /*assertEquals("Rocio", banco.getCuentas().stream().filter
                (c -> c.getPersona().equals("Rocio")).
                findFirst().get().getPersona());*/
        /*assertTrue(banco.getCuentas().stream().
                anyMatch(c -> c.getPersona().equals("Dafne")));*/
    }


    @Nested
    class SistemaOperativoTest{
        @Test
        @EnabledOnOs(OS.WINDOWS)
        void soloWindows() {
        }

        //Se deahabilita para windows y pasa el test igual
        @Test
        @EnabledOnOs({OS.MAC, OS.LINUX})
        void soloMacLinux() {
        }

        @Test
        @DisabledOnOs(OS.WINDOWS)
        void testNoWindows() {
        }
    }

    @Nested
    class JavaVersionTest{
        @Test
        @EnabledOnJre(JRE.JAVA_8)
        void soloJdk8() {
        }

        @Test
        @EnabledOnJre(JRE.JAVA_19)
        void soloJdk19() {
        }

        @Test
        @DisabledOnJre(JRE.JAVA_19)
        void noJdk19() {
        }
    }

    @Nested
    class SystemPropertiesTest{
        @Test
        void imprimirSystemProperties() {
            Properties properties = System.getProperties();
            properties.forEach((k, v)-> System.out.println(k + " : " + v));
        }

        @Test
        @EnabledIfSystemProperty(named = "java.version", matches = "19")
        void testJavaVersion() {
        }

    }

    @Nested
    class VariableAmbienteTest{
        @Test
        void imprimirVariablesDeAmbiente() {
            Map<String, String> getenv = System.getenv();
            getenv.forEach((k, v)-> System.out.println(k + " = " + v));
        }

        @Test
        @EnabledIfEnvironmentVariable(named = "NUMBER_OF_PROCESSORS", matches = "16")
        void testProcesadores() {
        }

    }


    @Test
    @DisplayName("test saldo cuenta dev")
    //se deshabilita si no estoy con dev
    void test_saldo_cuenta_Dev() {
        boolean esDev = "dev".equals(System.getProperty("ENV"));
        assumeTrue(esDev);
        assertNotNull(cuenta.getSaldo());
        assertEquals(1000.12345, cuenta.getSaldo().doubleValue());
        assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    @DisplayName("test saldo cuenta dev 2")
    //se deshabilitan partes si no estoy con dev
    void test_saldo_cuenta_Dev2() {
        boolean esDev = "dev".equals(System.getProperty("ENV"));
        assumingThat(esDev, () -> {
            assertNotNull(cuenta.getSaldo());
            assertEquals(1000.12345, cuenta.getSaldo().doubleValue());
            assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        });
    }

    @RepeatedTest(5)
    void testDebitoCuenta(RepetitionInfo info) {
        //cuenta = new Cuenta("Rocio", new BigDecimal("1000.12345"));
        if(info.getCurrentRepetition() == 3){
            System.out.println("Estamos en la repeticion " +info.getCurrentRepetition());
        }
        cuenta.debito(new BigDecimal(100));
        assertNotNull(cuenta.getSaldo());
        assertEquals(900, cuenta.getSaldo().intValue());
        assertEquals("900.12345",cuenta.getSaldo().toPlainString());
    }

    @ParameterizedTest
    @ValueSource(doubles = {100, 200, 300, 1000})
    void testDebitoCuentaValueSource(double monto) {
        cuenta.debito(new BigDecimal(monto));
        assertNotNull(cuenta.getSaldo());
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
    }

    @ParameterizedTest
    @CsvSource({"1,100", "2,200", "3,300", "4,1000"})
    void testDebitoCuentaCsvSource(String index, String monto) {
        System.out.println(index + " : " + monto);
        cuenta.debito(new BigDecimal(monto));
        assertNotNull(cuenta.getSaldo());
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
    }


}