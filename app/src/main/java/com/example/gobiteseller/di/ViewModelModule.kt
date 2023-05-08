package com.example.gobiteseller.di


import com.example.gobiteseller.ui.home.HomeViewModel
import com.example.gobiteseller.ui.login.LoginViewModel
import com.example.gobiteseller.ui.menu.MenuViewModel
import com.example.gobiteseller.ui.menuItem.MenuItemViewModel
import com.example.gobiteseller.ui.signup.SignUpViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { HomeViewModel(get(),get(),get()) }
    viewModel { MenuItemViewModel(get(),get()) }
    viewModel { MenuViewModel(get()) }
//    viewModel {
//        OrderViewModel(
//            get(),
//            get(),
//            get(),
//            get()
//        )
//    }
//    viewModel { SearchOrderViewModel(get()) }
//    viewModel { OrderHistoryViewModel(get()) }
//    viewModel { ProfileViewModel(get(),get()) }
//    viewModel { OTPViewModel(get()) }
    viewModel { LoginViewModel(get()) }
//    viewModel { ShopProfileViewModel(get()) }
//    viewModel { OrderDetailViewModel(get()) }
//    viewModel { ContributorViewModel() }
    viewModel { SignUpViewModel(get()) }
}
