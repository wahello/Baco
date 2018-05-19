package live.gaskell.baco.MainActivityFragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.RecursiveAction;

import javax.annotation.Nonnull;

import butterknife.ButterKnife;
import live.gaskell.baco.AllCategoriaProductosQuery;
import live.gaskell.baco.ApolloClient.Apollo_Client;
import live.gaskell.baco.ItemFast.ItemFastCategoria;
import live.gaskell.baco.R;

public class FragmentMainActivityCarta extends Fragment {
    private View rootView;

    private RecyclerView recyclerView;

    private ItemAdapter itemAdapterCategorias;
    private FastAdapter fastAdapterCategorias;
    private List<ItemFastCategoria> itemFastCategoriaList;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_mainactivity_carta, container, false);
        ButterKnife.bind(rootView);

        recyclerView = rootView.findViewById(R.id.recyclerView);
        itemFastCategoriaList = new ArrayList<>();

        itemAdapterCategorias = new ItemAdapter();
        fastAdapterCategorias = FastAdapter.with(itemAdapterCategorias);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(rootView.getContext(), 2);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(fastAdapterCategorias);

        viewDataCategorias();

        return rootView;
    }

    /**
     * Consulta en graphql todas las categorias
     * Prepara los datos para crear la vista
     * Muestra la vista
     */
    private void viewDataCategorias() {
        ApolloCall.Callback<AllCategoriaProductosQuery.Data> callback =
                new ApolloCall.Callback<AllCategoriaProductosQuery.Data>() {
                    @Override
                    public void onResponse(@Nonnull final Response<AllCategoriaProductosQuery.Data> response) {
                        Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                for (AllCategoriaProductosQuery.AllCategoriasProducto data : response.data().allCategoriasProductoes()) {
                                    itemFastCategoriaList.add(new ItemFastCategoria()
                                            .withId(data.id())
                                            .withNombre(data.nombre())
                                    );
                                }
                                itemAdapterCategorias.add(itemFastCategoriaList);
                            }
                        });
                    }

                    @Override
                    public void onFailure(@Nonnull ApolloException e) {
                        Toast.makeText(rootView.getContext(), "No se pudo obtener los datos", Toast.LENGTH_SHORT).show();
                    }
                };
        Apollo_Client.getApolloClientLogin(
                getContext())
                .query(AllCategoriaProductosQuery
                        .builder().build())
                .enqueue(callback);
    }
}