package com.example.noman_000.android_recipe_maker.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.noman_000.android_recipe_maker.Ingredients_List_Search;
import com.example.noman_000.android_recipe_maker.Interfaces.SetCheckbox;
import com.example.noman_000.android_recipe_maker.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class Ingredients_Adapter extends RecyclerView.Adapter<Ingredients_Adapter.ViewHolder> {
    private ArrayList<String> ingredients;
    private Context context;
    private final LayoutInflater inflater;
    private static SetCheckbox setCheckbox;
    public Ingredients_Adapter(@NonNull ArrayList<String> ingredients, @NonNull Context context, SetCheckbox setCheckbox){
        this.ingredients = ingredients;
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.setCheckbox = setCheckbox;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =  inflater.inflate(R.layout.ingredient, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.checkBox.setText(ingredients.get(position));
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }
    static class ViewHolder extends RecyclerView.ViewHolder{
        private CheckBox checkBox;
        private ViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.selectIngredient);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    TextView textView = (TextView) ((LinearLayout)checkBox.getParent().
                            getParent()).getChildAt(0);
                    String key = textView.getText().toString();
                    if(isChecked){
                        ArrayList<String> input = Ingredients_List_Search.userSelection.get(key);
                        if(input == null){
                            input = new ArrayList<>();
                            input.add((compoundButton.getText().toString()).toLowerCase());
                            Ingredients_List_Search.userSelection.put(key, input);
                        }
                        else{
                            input.add((compoundButton.getText().toString()).toLowerCase());
                            Log.d("onCheckedChanged : ", "checked " + getAdapterPosition() +
                                    textView.getText().toString() + " " + compoundButton.getText());
                        }
                        setCheckbox.setCheckBox(compoundButton);
                    }
                    else{
                        Log.d("onCheckedChanged", "not checked");
                        ArrayList<String> input = Ingredients_List_Search.userSelection.get(key);
                        input.remove((compoundButton.getText().toString()).toLowerCase());
                        setCheckbox.reMoveCheckbox(compoundButton);
                        if(input.size() == 0){
                            Ingredients_List_Search.userSelection.remove(key);
                        }
                    }
                }
            });
        }
    }
}
