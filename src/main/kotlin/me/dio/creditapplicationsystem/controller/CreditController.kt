package me.dio.creditapplicationsystem.controller

import jakarta.validation.Valid
import me.dio.creditapplicationsystem.dto.CreateCreditDto
import me.dio.creditapplicationsystem.dto.CreditResponse
import me.dio.creditapplicationsystem.dto.CreditResponseListDto
import me.dio.creditapplicationsystem.entity.Credit
import me.dio.creditapplicationsystem.service.impl.CreditService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID
import java.util.stream.Collectors

@RestController
@RequestMapping("/api/credits")
class CreditController (
    private val creditService: CreditService
){
    @PostMapping
    fun saveCredit(@RequestBody @Valid createCreditDto: CreateCreditDto): ResponseEntity<String> {
       val credit: Credit = this.creditService.save(createCreditDto.toEntity())

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body("Credit ${credit.creditCode} - Customer ${credit.customer?.firstName} saved!")
    }

    @GetMapping
    fun findAllByCustomerId(@RequestParam(value="customerId") customerId: Long): ResponseEntity<List<CreditResponseListDto>>{
        val creditResponseList: List<CreditResponseListDto> =
            this.creditService.findAllByCustomer(customerId).stream()
            .map{credit: Credit -> CreditResponseListDto(credit)}
            .collect(Collectors.toList())

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(creditResponseList)
    }

    @GetMapping("/{creditCode}")
    fun findByCreditCode(@RequestParam(value="customerId") customerId: Long,
                         @PathVariable creditCode: UUID) : ResponseEntity<CreditResponse> {
        val credit: Credit = this.creditService.findByCreditCode(customerId, creditCode)
        val creditResponse = CreditResponse(credit)

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(creditResponse)
    }
}