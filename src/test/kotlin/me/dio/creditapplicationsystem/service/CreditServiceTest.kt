package me.dio.creditapplicationsystem.service

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import me.dio.creditapplicationsystem.entity.Address
import me.dio.creditapplicationsystem.entity.Credit
import me.dio.creditapplicationsystem.entity.Customer
import me.dio.creditapplicationsystem.enumeration.Status
import me.dio.creditapplicationsystem.repository.CreditRepository
import me.dio.creditapplicationsystem.service.impl.CreditService
import me.dio.creditapplicationsystem.service.impl.CustomerService
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.ActiveProfiles
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*

@ActiveProfiles("test")
@ExtendWith(MockKExtension::class)
class CreditServiceTest {
    @MockK lateinit var creditRepository: CreditRepository
    @MockK lateinit var customerService: CustomerService
    @InjectMockKs lateinit var creditService: CreditService

    @Test
    fun `should create credit`(){
        //given
        val fakeCredit = buildCredit()
        val fakeCustomer = buildCustomer()
        every { creditRepository.save(any()) } returns fakeCredit
        every { customerService.findById(any())} returns fakeCustomer
        //when
        val actual: Credit = creditService.save(fakeCredit)
        //then
        Assertions.assertThat(actual).isNotNull
        Assertions.assertThat(actual).isSameAs(fakeCredit)
        verify (exactly = 1){ creditRepository.save(fakeCredit) }
    }

    @Test
    fun `should find list of credit by customer id`(){
        //given
        val fakeCustomerId = Random().nextLong()
        val fakeCustomer = buildCustomer(id=fakeCustomerId)
        val fakeCreditList = List(5) {
            buildCredit(customer = fakeCustomer, id = Random().nextLong(), creditCode = UUID.randomUUID())
        }
        every { creditRepository.findAllByCustomer(fakeCustomerId) } returns fakeCreditList
        //when
        val actual: List<Credit> = creditService.findAllByCustomer(fakeCustomerId)
        //then
        Assertions.assertThat(actual).isNotNull
        Assertions.assertThat(actual).isNotEmpty
        Assertions.assertThat(actual).isEqualTo(fakeCreditList)
        Assertions.assertThat(actual).hasSize(5)
        Assertions.assertThat(actual).containsAll(fakeCreditList)
        Assertions.assertThat(actual).containsExactlyElementsOf(fakeCreditList)
        Assertions.assertThat(actual).allSatisfy { it.creditValue > BigDecimal.ZERO }
        Assertions.assertThat(actual).allMatch { it.status == Status.IN_PROGRESS }
        verify (exactly = 1){ creditRepository.findAllByCustomer(fakeCustomerId) }
    }


    private fun buildCredit(
        creditCode: UUID = UUID.fromString("3d5b7d4a-a032-11ee-8c90-0242ac120002"),
        creditValue: BigDecimal = BigDecimal.valueOf(1000.0),
        dayFirstInstallment: LocalDate = LocalDate.now(),
        numberOfInstallment: Int = 5,
        status: Status = Status.IN_PROGRESS,
        customer: Customer = buildCustomer(),
        id: Long = 1L
    ) = Credit(
        creditCode = creditCode,
        creditValue = creditValue,
        dayFirstInstallment = dayFirstInstallment,
        numberOfInstallment = numberOfInstallment,
        status = status,
        customer = customer,
        id = id
    )

    private fun buildCustomer(
        firstName: String = "Joao",
        lastName: String = "Fulano",
        cpf: String = "1234567890",
        email: String = "joao@mail.com",
        income: BigDecimal = BigDecimal.valueOf(1000.0),
        password: String = "12345",
        zipCode: String = "12345",
        street: String = "Rua do Joao",
        id: Long = 1L
    ) = Customer(
        firstName = firstName,
        lastName = lastName,
        cpf = cpf,
        email = email,
        income = income,
        password = password,
        address = Address(
            zipCode = zipCode,
            street = street,
        ),
        id = id
    )

}