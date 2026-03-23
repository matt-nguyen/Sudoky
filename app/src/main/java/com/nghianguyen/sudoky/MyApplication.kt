package com.nghianguyen.sudoky

import android.app.Application
import com.nghianguyen.data.scanner.dataScannerModule
import com.nghianguyen.data.scanner.mlkit.dataScannerMlKitModule
import com.nghianguyen.data.sudoku.dataSudokuModule
import com.nghianguyen.data.sudoku.local.dataSudokuRoomModule
import com.nghianguyen.feature.home.featureHomeModule
import com.nghianguyen.feature.scanner.featureScannerModule
import com.nghianguyen.feature.sudoku.featureSudokuModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@MyApplication)

            modules(
                featureHomeModule,
                featureScannerModule,
                featureSudokuModule,
                dataSudokuModule,
                dataSudokuRoomModule,
                dataScannerModule,
                dataScannerMlKitModule,
                //                module {
                //                    viewModel<MainViewModel> {
                //                        MainViewModel(get())
                //                    }
                //                }
            )
        }
    }
}
