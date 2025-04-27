/*
 * Copyright (c) 2025.
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.expensemanager.controller;

import com.expensemanager.exception.ResourceNotFoundException;
import com.expensemanager.model.Expense;
import com.expensemanager.model.User;
import com.expensemanager.repo.ExpenseRepo;
import com.expensemanager.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author amanjain
 **/
@RestController
@RequestMapping("/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseRepo expenseRepo;
    private final UserRepo userRepo;

    @GetMapping
    public List<Expense> getMyExpenses() {
        System.out.println("Accessed by: " + SecurityContextHolder.getContext().getAuthentication().getName());
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepo.findByEmail(email).orElseThrow();
        return expenseRepo.findByUser(user);
    }

    @PostMapping
    public Expense createExpense(@RequestBody Expense expense){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepo.findByEmail(email).orElseThrow();
        expense.setUser(user);
        return expenseRepo.save(expense);
    }

    @PutMapping("/{id}")
    public Expense updateExpense(@PathVariable Long id, @RequestBody Expense updatedExpense) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepo.findByEmail(email).orElseThrow();

        Expense expense = expenseRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense Not Found"));
        if(!expense.getUser().getId().equals(user.getId())){
            throw new RuntimeException("Unauthorized Access to Expense");
        }

        expense.setTitle(updatedExpense.getTitle());
        expense.setCategory(updatedExpense.getCategory());
        expense.setAmount(updatedExpense.getAmount());
        expense.setDate(updatedExpense.getDate());

        return expenseRepo.save(expense);
    }

    @DeleteMapping("/{id}")
    public String deleteExpense(@PathVariable Long id){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepo.findByEmail(email).orElseThrow();

        Expense expense = expenseRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense Not Found"));
        if(!expense.getUser().getId().equals(user.getId())){
            throw new RuntimeException("Unauthorized Access to Expense");
        }

        expenseRepo.delete(expense);
        return "Expense Deleted Successfully";
    }
}
