package com.example.qlcc



import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class VehicleActivity : AppCompatActivity() {

    lateinit var vehicleList: ArrayList<String>
    lateinit var filteredList: ArrayList<String>
    lateinit var adapter: ArrayAdapter<String>

    lateinit var tvAvailable: TextView
    lateinit var tvStatus: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vehicle)

        // ===== ÁNH XẠ =====
        val btnShowList = findViewById<Button>(R.id.btnShowList)
        val btnAdd = findViewById<Button>(R.id.btnAddVehicle)
        val layoutList = findViewById<LinearLayout>(R.id.layoutListContainer)
        val listView = findViewById<ListView>(R.id.listVehicle)
        val edtSearch = findViewById<EditText>(R.id.edtSearch)

        tvAvailable = findViewById(R.id.tvAvailable)
        tvStatus = findViewById(R.id.tvStatus)

        // ===== DATA =====
        vehicleList = ArrayList()
        filteredList = ArrayList()

        // ===== ADAPTER =====
        adapter = object : ArrayAdapter<String>(
            this,
            R.layout.item_vehicle,
            R.id.txtVehicle,
            filteredList
        ) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

                val view = layoutInflater.inflate(R.layout.item_vehicle, parent, false)

                val txt = view.findViewById<TextView>(R.id.txtVehicle)
                val btnDelete = view.findViewById<Button>(R.id.btnDelete)

                val item = filteredList[position]
                txt.text = item

                btnDelete.setOnClickListener {
                    vehicleList.remove(item)
                    filteredList.remove(item)
                    notifyDataSetChanged()

                    updateStatus() // 👈 cập nhật trạng thái

                    Toast.makeText(context, "Đã xóa", Toast.LENGTH_SHORT).show()
                }

                return view
            }
        }

        listView.adapter = adapter

        // ===== HIỆN / ẨN LIST =====
        btnShowList.setOnClickListener {
            if (layoutList.visibility == View.GONE) {
                layoutList.visibility = View.VISIBLE
                btnShowList.text = "Ẩn danh sách"
            } else {
                layoutList.visibility = View.GONE
                btnShowList.text = "Hiển thị danh sách xe"
            }
        }

        // ===== SEARCH =====
        edtSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                val keyword = s.toString().lowercase()
                filteredList.clear()

                if (keyword.isEmpty()) {
                    filteredList.addAll(vehicleList)
                } else {
                    for (item in vehicleList) {
                        if (item.lowercase().contains(keyword)) {
                            filteredList.add(item)
                        }
                    }
                }

                adapter.notifyDataSetChanged()
            }
        })

        // ===== THÊM XE =====
        btnAdd.setOnClickListener {

            val builder = AlertDialog.Builder(this)
            builder.setTitle("Thêm xe")

            val layout = LinearLayout(this)
            layout.orientation = LinearLayout.VERTICAL
            layout.setPadding(50, 20, 50, 10)

            val edtPlate = EditText(this)
            edtPlate.hint = "Biển số xe"

            val edtOwner = EditText(this)
            edtOwner.hint = "Tên chủ xe"

            val edtApartment = EditText(this)
            edtApartment.hint = "Tên căn hộ"

            layout.addView(edtPlate)
            layout.addView(edtOwner)
            layout.addView(edtApartment)

            builder.setView(layout)

            builder.setPositiveButton("Thêm") { _, _ ->

                val plate = edtPlate.text.toString().trim()
                val owner = edtOwner.text.toString().trim()
                val apartment = edtApartment.text.toString().trim()

                if (plate.isNotEmpty() && owner.isNotEmpty() && apartment.isNotEmpty()) {

                    val newVehicle = "$owner - $plate - $apartment"

                    vehicleList.add(newVehicle)
                    filteredList.add(newVehicle)

                    adapter.notifyDataSetChanged()

                    updateStatus() // 👈 cập nhật trạng thái

                    Toast.makeText(this, "Đã thêm xe!", Toast.LENGTH_SHORT).show()

                } else {
                    Toast.makeText(this, "Nhập đầy đủ!", Toast.LENGTH_SHORT).show()
                }
            }

            builder.setNegativeButton("Hủy", null)
            builder.show()
        }

        updateStatus() // 👈 init ban đầu
    }

    // ===== HÀM CẬP NHẬT TRẠNG THÁI =====
    fun updateStatus() {
        val total = 100
        val current = vehicleList.size

        tvAvailable.text = "Còn: $current/$total"

        if (current >= total) {
            tvStatus.text = "Full"
            tvStatus.setTextColor(Color.RED)
        } else {
            tvStatus.text = "Available"
            tvStatus.setTextColor(Color.GREEN)
        }
    }
}
