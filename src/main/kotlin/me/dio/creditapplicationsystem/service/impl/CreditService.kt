package me.dio.creditapplicationsystem.service.impl

import me.dio.creditapplicationsystem.entity.Credit
import me.dio.creditapplicationsystem.repository.CreditRepository
import me.dio.creditapplicationsystem.service.ICreditService
import java.util.*

class CreditService(
    private val creditRepository: CreditRepository,
    private val customerService: CustomerService
): ICreditService {
    override fun save(credit: Credit): Credit {
      credit.apply {
          customer = customerService.findById(credit.customer?.id!!)
      }
      return this.creditRepository.save(credit)
    }

    override fun findAllByCustomer(customerId: Long): List<Credit> {
        return this.creditRepository.findAllByCustomer(customerId)
    }

    override fun findByCreditCode(customerId: Long,creditCode: UUID): Credit {
       val credit = this.creditRepository.findByCreditCode(creditCode)
            ?: throw RuntimeException("Credit code $creditCode not found")

       return if (credit.customer?.id == customerId) credit
              else throw RuntimeException("Contact Admin")
    }
}